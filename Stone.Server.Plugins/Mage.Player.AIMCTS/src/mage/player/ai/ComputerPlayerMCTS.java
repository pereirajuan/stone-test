package mage.player.ai;

import mage.abilities.Ability;
import mage.abilities.ActivatedAbility;
import mage.abilities.common.PassAbility;
import mage.cards.Card;
import mage.constants.PhaseStep;
import mage.constants.RangeOfInfluence;
import mage.constants.Zone;
import mage.game.Game;
import mage.game.combat.Combat;
import mage.game.combat.CombatGroup;
import mage.player.ai.MCTSPlayer.NextAction;
import mage.players.Player;
import mage.util.ThreadUtils;
import mage.util.XmageThreadFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author BetaSteward_at_googlemail.com
 */
public class ComputerPlayerMCTS extends ComputerPlayer {

    private static final int THINK_MIN_RATIO = 40;
    private static final int THINK_MAX_RATIO = 100;
    private static final double THINK_TIME_MULTIPLIER = 2.0;
    private static final boolean USE_MULTIPLE_THREADS = true;

    protected transient MCTSNode root;
    protected int maxThinkTime;
    private static final Logger logger = Logger.getLogger(ComputerPlayerMCTS.class);
    private int poolSize;

    private ExecutorService threadPoolSimulations = null;

    public ComputerPlayerMCTS(String name, RangeOfInfluence range, int skill) {
        super(name, range);
        human = false;
        maxThinkTime = (int) (skill * THINK_TIME_MULTIPLIER);
        poolSize = Runtime.getRuntime().availableProcessors();
    }

    protected ComputerPlayerMCTS(UUID id) {
        super(id);
    }

    public ComputerPlayerMCTS(final ComputerPlayerMCTS player) {
        super(player);
    }

    @Override
    public ComputerPlayerMCTS copy() {
        return new ComputerPlayerMCTS(this);
    }

    protected String lastPhase = "";

    @Override
    public boolean priority(Game game) {
        if (game.getTurnStepType() == PhaseStep.UPKEEP) {
            if (!lastPhase.equals(game.getTurn().getValue(game.getTurnNum()))) {
                logList(game.getTurn().getValue(game.getTurnNum()) + name + " hand: ", new ArrayList(hand.getCards(game)));
                lastPhase = game.getTurn().getValue(game.getTurnNum());
                if (MCTSNode.USE_ACTION_CACHE) {
                    int count = MCTSNode.cleanupCache(game.getTurnNum());
                    if (count > 0)
                        logger.info("Removed " + count + " cache entries");
                }
            }
        }
        game.getState().setPriorityPlayerId(playerId);
        game.firePriorityEvent(playerId);
        getNextAction(game, NextAction.PRIORITY);
        Ability ability = root.getAction();
        if (ability == null)
            logger.fatal("null ability");
        activateAbility((ActivatedAbility) ability, game);
        if (ability instanceof PassAbility)
            return false;
        logLife(game);
        logger.info("choose action:" + root.getAction() + " success ratio: " + root.getWinRatio());
        return true;
    }

    protected void calculateActions(Game game, NextAction action) {
        if (root == null) {
            Game sim = createMCTSGame(game);
            MCTSPlayer player = (MCTSPlayer) sim.getPlayer(playerId);
            player.setNextAction(action);
            root = new MCTSNode(playerId, sim);
        }
        applyMCTS(game, action);
        if (root != null && root.bestChild() != null) {
            root = root.bestChild();
            root.emancipate();
        }
    }

    protected void getNextAction(Game game, NextAction nextAction) {
        if (root != null) {
            MCTSNode newRoot;
            newRoot = root.getMatchingState(game.getState().getValue(game, playerId));
            if (newRoot != null) {
                newRoot.emancipate();
            } else
                logger.info("unable to find matching state");
            root = newRoot;
        }
        calculateActions(game, nextAction);
    }

    @Override
    public void selectAttackers(Game game, UUID attackingPlayerId) {
        StringBuilder sb = new StringBuilder();
        sb.append(game.getTurn().getValue(game.getTurnNum())).append(" player ").append(name).append(" attacking with: ");
        getNextAction(game, NextAction.SELECT_ATTACKERS);
        Combat combat = root.getCombat();
        UUID opponentId = game.getCombat().getDefenders().iterator().next();
        for (UUID attackerId : combat.getAttackers()) {
            this.declareAttacker(attackerId, opponentId, game, false);
            sb.append(game.getPermanent(attackerId).getName()).append(',');
        }
        logger.info(sb.toString());
        MCTSNode.logHitMiss();
    }

    @Override
    public void selectBlockers(Ability source, Game game, UUID defendingPlayerId) {
        StringBuilder sb = new StringBuilder();
        sb.append(game.getTurn().getValue(game.getTurnNum())).append(" player ").append(name).append(" blocking: ");
        getNextAction(game, NextAction.SELECT_BLOCKERS);
        Combat simulatedCombat = root.getCombat();
        List<CombatGroup> currentGroups = game.getCombat().getGroups();
        for (int i = 0; i < currentGroups.size(); i++) {
            if (i < simulatedCombat.getGroups().size()) {
                CombatGroup currentGroup = currentGroups.get(i);
                CombatGroup simulatedGroup = simulatedCombat.getGroups().get(i);
                sb.append(game.getPermanent(currentGroup.getAttackers().get(0)).getName()).append(" with: ");
                for (UUID blockerId : simulatedGroup.getBlockers()) {
                    // blockers can be added automaticly by requirement effects, so we must add only missing blockers
                    if (!currentGroup.getBlockers().contains(blockerId)) {
                        this.declareBlocker(this.getId(), blockerId, currentGroup.getAttackers().get(0), game);
                        sb.append(game.getPermanent(blockerId).getName()).append(',');
                    }
                }
                sb.append('|');
            }
        }
        logger.info(sb.toString());
        MCTSNode.logHitMiss();
    }

    protected long totalThinkTime = 0;
    protected long totalSimulations = 0;

    protected void applyMCTS(final Game game, final NextAction action) {

        int thinkTime = calculateThinkTime(game, action);

        if (thinkTime > 0) {
            if (USE_MULTIPLE_THREADS) {
                if (this.threadPoolSimulations == null) {
                    // same params as Executors.newFixedThreadPool
                    // no needs errors check in afterExecute here cause that pool used for FutureTask with result check already
                    this.threadPoolSimulations = new ThreadPoolExecutor(
                            poolSize,
                            poolSize,
                            0L,
                            TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>(),
                            new XmageThreadFactory(ThreadUtils.THREAD_PREFIX_AI_SIMULATION_MCTS) // TODO: add player/game to thread name?
                    );
                }

                List<MCTSExecutor> tasks = new ArrayList<>();
                for (int i = 0; i < poolSize; i++) {
                    Game sim = createMCTSGame(game);
                    MCTSPlayer player = (MCTSPlayer) sim.getPlayer(playerId);
                    player.setNextAction(action);
                    MCTSExecutor exec = new MCTSExecutor(sim, playerId, thinkTime);
                    tasks.add(exec);
                }

                try {
                    List<Future<Boolean>> runningTasks = threadPoolSimulations.invokeAll(tasks, thinkTime, TimeUnit.SECONDS);
                    for (Future<Boolean> runningTask : runningTasks) {
                        runningTask.get();
                    }
                } catch (InterruptedException | CancellationException e) {
                    logger.warn("applyMCTS timeout");
                } catch (ExecutionException e) {
                    // real games: must catch and log
                    // unit tests: must raise again for fast fail
                    if (this.isTestMode() && this.isFastFailInTestMode()) {
                        throw new IllegalStateException("One of the simulated games raise the error: " + e, e);
                    }
                }

                int simCount = 0;
                for (MCTSExecutor task : tasks) {
                    simCount += task.getSimCount();
                    root.merge(task.getRoot());
                    task.clear();
                }
                tasks.clear();
                totalThinkTime += thinkTime;
                totalSimulations += simCount;
                logger.info("Player: " + name + " Simulated " + simCount + " games in " + thinkTime + " seconds - nodes in tree: " + root.size());
                logger.info("Total: Simulated " + totalSimulations + " games in " + totalThinkTime + " seconds - Average: " + totalSimulations / totalThinkTime);
                MCTSNode.logHitMiss();
            } else {
                long startTime = System.nanoTime();
                long endTime = startTime + (thinkTime * 1000000000l);
                MCTSNode current;
                int simCount = 0;
                while (true) {
                    long currentTime = System.nanoTime();
                    if (currentTime > endTime)
                        break;
                    current = root;

                    // Selection
                    while (!current.isLeaf()) {
                        current = current.select(this.playerId);
                    }

                    int result;
                    if (!current.isTerminal()) {
                        // Expansion
                        current.expand();

                        // Simulation
                        current = current.select(this.playerId);
                        result = current.simulate(this.playerId);
                        simCount++;
                    } else {
                        result = current.isWinner(this.playerId) ? 1 : -1;
                    }
                    // Backpropagation
                    current.backpropagate(result);
                }
                logger.info("Simulated " + simCount + " games - nodes in tree: " + root.size());
            }
//            displayMemory();
        }

    }

    //try to ensure that there are at least THINK_MIN_RATIO simulations per node at all times
    private int calculateThinkTime(Game game, NextAction action) {
        int thinkTime;
        int nodeSizeRatio = 0;
        if (root.getNumChildren() > 0)
            nodeSizeRatio = root.getVisits() / root.getNumChildren();
//        logger.info("Ratio: " + nodeSizeRatio);
        PhaseStep curStep = game.getTurnStepType();
        if (action == NextAction.SELECT_ATTACKERS || action == NextAction.SELECT_BLOCKERS) {
            if (nodeSizeRatio < THINK_MIN_RATIO) {
                thinkTime = maxThinkTime;
            } else if (nodeSizeRatio >= THINK_MAX_RATIO) {
                thinkTime = 0;
            } else {
                thinkTime = maxThinkTime / 2;
            }
        } else if (game.isActivePlayer(playerId) && (curStep == PhaseStep.PRECOMBAT_MAIN || curStep == PhaseStep.POSTCOMBAT_MAIN) && game.getStack().isEmpty()) {
            if (nodeSizeRatio < THINK_MIN_RATIO) {
                thinkTime = maxThinkTime;
            } else if (nodeSizeRatio >= THINK_MAX_RATIO) {
                thinkTime = 0;
            } else {
                thinkTime = maxThinkTime / 2;
            }
        } else {
            if (nodeSizeRatio < THINK_MIN_RATIO) {
                thinkTime = maxThinkTime / 2;
            } else {
                thinkTime = 0;
            }
        }
        return thinkTime;
    }

    /**
     * Copies game and replaces all players in copy with mcts players
     * Shuffles each players library so that there is no knowledge of its order
     * Swaps all other players hands with random cards from the library so that
     * there is no knowledge of what cards are in opponents hands
     * The most knowledge that is known is what cards are in an opponents deck
     *
     * @param game
     * @return a new game object with simulated players
     */
    protected Game createMCTSGame(Game game) {
        Game mcts = game.createSimulationForAI();

        for (Player copyPlayer : mcts.getState().getPlayers().values()) {
            Player origPlayer = game.getState().getPlayers().get(copyPlayer.getId());
            MCTSPlayer newPlayer = new MCTSPlayer(copyPlayer.getId());
            newPlayer.restore(origPlayer);
            newPlayer.setMatchPlayer(origPlayer.getMatchPlayer());
            if (!newPlayer.getId().equals(playerId)) {
                int handSize = newPlayer.getHand().size();
                newPlayer.getLibrary().addAll(newPlayer.getHand().getCards(mcts), mcts);
                newPlayer.getHand().clear();
                newPlayer.getLibrary().shuffle();
                for (int i = 0; i < handSize; i++) {
                    Card card = newPlayer.getLibrary().drawFromTop(mcts);
                    card.setZone(Zone.HAND, mcts);
                    newPlayer.getHand().add(card);
                }
            } else {
                newPlayer.getLibrary().shuffle();
            }
            mcts.getState().getPlayers().put(copyPlayer.getId(), newPlayer);
        }
        mcts.resume();
        return mcts;
    }

    protected void displayMemory() {
        long heapSize = Runtime.getRuntime().totalMemory();
        long heapMaxSize = Runtime.getRuntime().maxMemory();
        long heapFreeSize = Runtime.getRuntime().freeMemory();
        long heapUsedSize = heapSize - heapFreeSize;
        long mb = 1024 * 1024;

        logger.info("Max heap size: " + heapMaxSize / mb + " Heap size: " + heapSize / mb + " Used: " + heapUsedSize / mb);
    }

    protected void logLife(Game game) {
        StringBuilder sb = new StringBuilder();
        sb.append(game.getTurn().getValue(game.getTurnNum()));
        for (Player player : game.getPlayers().values()) {
            sb.append("[player ").append(player.getName()).append(':').append(player.getLife()).append(']');
        }
        logger.info(sb.toString());
    }

}
