/*
 *  Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and should not be interpreted as representing official policies, either expressed
 *  or implied, of BetaSteward_at_googlemail.com.
 */

package mage.player.ai;

import mage.Constants.Outcome;
import mage.Constants.PhaseStep;
import mage.Constants.RangeOfInfluence;
import mage.abilities.Ability;
import mage.abilities.ActivatedAbility;
import mage.abilities.common.PassAbility;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.costs.mana.ManaCost;
import mage.abilities.costs.mana.ManaCosts;
import mage.abilities.costs.mana.VariableManaCost;
import mage.abilities.effects.Effect;
import mage.abilities.effects.SearchEffect;
import mage.cards.Card;
import mage.cards.Cards;
import mage.choices.Choice;
import mage.filter.FilterAbility;
import mage.filter.common.FilterCreatureForAttack;
import mage.game.Game;
import mage.game.combat.Combat;
import mage.game.combat.CombatGroup;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.game.stack.StackAbility;
import mage.game.stack.StackObject;
import mage.game.turn.*;
import mage.players.Player;
import mage.target.Target;
import mage.target.TargetCard;
import mage.util.Logging;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 *
 * @author nantuko
 */
public class ComputerPlayer6 extends ComputerPlayer<ComputerPlayer6> implements Player {

	private static final transient org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ComputerPlayer6.class);
	private static final ExecutorService pool = Executors.newFixedThreadPool(1);

	protected int maxDepth;
	protected int maxNodes;
	protected int maxThink;
	protected LinkedList<Ability> actions = new LinkedList<Ability>();
	protected List<UUID> targets = new ArrayList<UUID>();
	protected List<String> choices = new ArrayList<String>();
	protected Combat combat;
	protected int currentScore;
	protected SimulationNode2 root;

	public ComputerPlayer6(String name, RangeOfInfluence range, int skill) {
		super(name, range);
		maxDepth = skill * 2;
		maxThink = skill * 3;
		maxNodes = Config2.maxNodes;
	}

	public ComputerPlayer6(final ComputerPlayer6 player) {
		super(player);
		this.maxDepth = player.maxDepth;
		this.currentScore = player.currentScore;
		if (player.combat != null)
			this.combat = player.combat.copy();
		for (Ability ability: player.actions) {
			actions.add(ability);
		}
		for (UUID targetId: player.targets) {
			targets.add(targetId);
		}
		for (String choice: player.choices) {
			choices.add(choice);
		}
	}

	@Override
	public ComputerPlayer6 copy() {
		return new ComputerPlayer6(this);
	}

	@Override
	public void priority(Game game) {
		logState(game);
		game.firePriorityEvent(playerId);
		switch (game.getTurn().getStepType()) {
			case UPKEEP:
			case DRAW:
				pass();
				break;
			case PRECOMBAT_MAIN:
			case DECLARE_BLOCKERS:
			case POSTCOMBAT_MAIN:
				if (game.getActivePlayerId().equals(playerId)) {
					printOutState(game, playerId);
					printOutState(game, game.getOpponents(playerId).iterator().next());
					if (actions.size() == 0) {
						calculateActions(game);
					}
					act(game);
				} else {
					pass();
				}
				break;
			case BEGIN_COMBAT:
			case COMBAT_DAMAGE:
			case END_COMBAT:
				pass();
				break;
			case DECLARE_ATTACKERS:
				if (!game.getActivePlayerId().equals(playerId)) {
					printOutState(game, playerId);
					printOutState(game, game.getOpponents(playerId).iterator().next());
					if (actions.size() == 0) {
						calculateActions(game);
					}
					act(game);
					//printOutState(game, playerId);
				} else {
					pass();
				}
				break;
			case END_TURN:
				pass();
				break;
			case CLEANUP:
				pass();
				break;
		}
	}

	protected void printOutState(Game game, UUID playerId) {
		Player player = game.getPlayer(playerId);
		System.out.println("Turn::"+game.getTurnNum());
		System.out.println("[" + game.getPlayer(playerId).getName() + "] " + game.getTurn().getStepType().name() +", life=" + player.getLife());
		Player opponent = game.getPlayer(game.getOpponents(playerId).iterator().next());
		System.out.println("[Opponent] life=" + opponent.getLife());

		String s = "[";
		for (Card card : player.getHand().getCards(game)) {
			s += card.getName() + ";";
		}
		s += "]";
		System.out.println("Hand: " + s);
		s = "[";
		for (Permanent permanent : game.getBattlefield().getAllPermanents()) {
			 if (permanent.getOwnerId().equals(player.getId())) {
				 s += permanent.getName();
				 if (permanent.isTapped()) {
					s+="(tapped)";
				 }
				 if (permanent.isAttacking()) {
					s+="(attacking)";
				 }
				 s+=";";
			 }
		}
		s += "]";
		System.out.println("Permanents: " + s);
	}

	protected void act(Game game) {
		if (actions == null || actions.size() == 0)
			pass();
		else {
			boolean usedStack = false;
			while (actions.peek() != null) {
				Ability ability = actions.poll();
				System.out.println("[" + game.getPlayer(playerId).getName() + "] Action: " + ability.toString());
				this.activateAbility((ActivatedAbility) ability, game);
				if (ability.isUsesStack())
					usedStack = true;
			}
			if (usedStack)
				pass();
		}
	}

	protected void calculateActions(Game game) {
		if (!getNextAction(game)) {
			Game sim = createSimulation(game);
			SimulationNode2.resetCount();
			root = new SimulationNode2(null, sim, maxDepth, playerId);
			logger.info("simulating actions");
			//int bestScore = addActionsTimed(new FilterAbility());
			currentScore = GameStateEvaluator2.evaluate(playerId, game);
			addActionsTimed(new FilterAbility());
			if (root.children.size() > 0) {
				root = root.children.get(0);
				//GameStateEvaluator2.evaluate(playerId, root.getGame());
				int bestScore = root.getScore();
				if (bestScore > currentScore) {
					actions = new LinkedList<Ability>(root.abilities);
					combat = root.combat;
				}
			}
		}
	}

	protected boolean getNextAction(Game game) {
		if (root != null && root.children.size() > 0) {
			SimulationNode2 test = root;
			root = root.children.get(0);
			while (root.children.size() > 0 && !root.playerId.equals(playerId)) {
				test = root;
				root = root.children.get(0);
			}
			logger.info("simlating -- game value:" + game.getState().getValue() + " test value:" + test.gameValue);
			if (root.playerId.equals(playerId) && root.abilities != null && game.getState().getValue() == test.gameValue) {

				/*
				// Try to fix horizon effect
				if (root.combat == null || root.combat.getAttackers().size() == 0) {
					FilterCreatureForAttack attackFilter = new FilterCreatureForAttack();
					attackFilter.getControllerId().add(playerId);
					List<Permanent> attackers = game.getBattlefield().getAllActivePermanents(attackFilter);
					if (attackers.size() > 0) {
						// we have attackers but don't attack with any of them
						// let's try once again to avoid possible horizon effect
						return false;
					}
				}
				*/

				logger.info("simulating -- continuing previous action chain");
				actions = new LinkedList<Ability>(root.abilities);
				combat = root.combat;
				return true;
			}
			else {
				return false;
			}
		}
		return false;
	}

	protected int minimaxAB(SimulationNode2 node, FilterAbility filter, int depth, int alpha, int beta) {
		UUID currentPlayerId = node.getGame().getPlayerList().get();
		SimulationNode2 bestChild = null;
		for (SimulationNode2 child: node.getChildren()) {
			Combat _combat = child.getCombat();
			if (alpha >= beta) {
				//logger.info("alpha beta pruning");
				break;
			}
			if (SimulationNode2.nodeCount > maxNodes) {
				//logger.info("simulating -- reached end-state, count=" + SimulationNode2.nodeCount);
				break;
			}
			int val = addActions(child, filter, depth-1, alpha, beta);
			if (!currentPlayerId.equals(playerId)) {
				if (val < beta) {
					beta = val;
					bestChild = child;
					if (node.getCombat() == null) {
						node.setCombat(_combat);
						bestChild.setCombat(_combat);
					}
				}
				// no need to check other actions
				if (val == GameStateEvaluator2.LOSE_GAME_SCORE) {
					logger.debug("lose - break");
					break;
				}
			}
			else {
				if (val > alpha) {
					alpha = val;
					bestChild = child;
					if (node.getCombat() == null) {
						node.setCombat(_combat);
						bestChild.setCombat(_combat);
					}
				}
				// no need to check other actions
				if (val == GameStateEvaluator2.WIN_GAME_SCORE) {
					logger.debug("win - break");
					break;
				}
			}
		}
		node.children.clear();
		if (bestChild != null)
			node.children.add(bestChild);
		if (!currentPlayerId.equals(playerId)) {
			//logger.info("returning minimax beta: " + beta);
			return beta;
		}
		else {
			//logger.info("returning minimax alpha: " + alpha);
			return alpha;
		}
	}

	protected SearchEffect getSearchEffect(StackAbility ability) {
		for (Effect effect: ability.getEffects()) {
			if (effect instanceof SearchEffect) {
				return (SearchEffect) effect;
			}
		}
		return null;
	}

	protected void resolve(SimulationNode2 node, int depth, Game game) {
		StackObject ability = game.getStack().pop();
		if (ability instanceof StackAbility) {
			SearchEffect effect = getSearchEffect((StackAbility) ability);
			if (effect != null && ability.getControllerId().equals(playerId)) {
				Target target = effect.getTarget();
				if (!target.doneChosing()) {
					for (UUID targetId: target.possibleTargets(ability.getSourceId(), ability.getControllerId(), game)) {
						Game sim = game.copy();
						StackAbility newAbility = (StackAbility) ability.copy();
						SearchEffect newEffect = getSearchEffect((StackAbility) newAbility);
						newEffect.getTarget().addTarget(targetId, newAbility, sim);
						sim.getStack().push(newAbility);
						SimulationNode2 newNode = new SimulationNode2(node, sim, depth, ability.getControllerId());
						node.children.add(newNode);
						newNode.getTargets().add(targetId);
						logger.debug("simulating search -- node#: " + SimulationNode2.getCount() + "for player: " + sim.getPlayer(ability.getControllerId()).getName());
					}
					return;
				}
			}
		}
		//logger.info("simulating resolve ");
		ability.resolve(game);
		game.applyEffects();
		game.getPlayers().resetPassed();
		game.getPlayerList().setCurrent(game.getActivePlayerId());
	}

	protected Integer addActionsTimed(final FilterAbility filter) {
		FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
			public Integer call() throws Exception
			{
				return addActions(root, filter, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
			}
		});
		pool.execute(task);
		try {
			return task.get(maxThink, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			logger.info("simulating - timed out");
			task.cancel(true);
		} catch (ExecutionException e) {
			e.printStackTrace();
			task.cancel(true);
		} catch (InterruptedException e) {
			e.printStackTrace();
			task.cancel(true);
		} catch (Exception e) {
			e.printStackTrace();
			task.cancel(true);
		}
		//TODO: timeout handling
		return 0;
	}

	protected int addActions(SimulationNode2 node, FilterAbility filter, int depth, int alpha, int beta) {
		logger.debug("addActions: " + depth + ", alpha=" + alpha + ", beta=" + beta);
		Game game = node.getGame();
		int val;
		if (Thread.interrupted()) {
			Thread.currentThread().interrupt();
			val = GameStateEvaluator2.evaluate(playerId, game);
			logger.info("interrupted - " + val);
			return val;
		}
		if (depth <= 0 || SimulationNode2.nodeCount > maxNodes || game.isGameOver()) {
			logger.debug("simulating -- reached end state, node count=" + SimulationNode2.nodeCount + ", depth=" + depth);
			val = GameStateEvaluator2.evaluate(playerId, game);
			UUID currentPlayerId = node.getGame().getPlayerList().get();
			//logger.info("reached - " + val + ", playerId=" + playerId + ", node.pid="+currentPlayerId);
			return val;
		}
		else if (node.getChildren().size() > 0) {
			logger.debug("simulating -- somthing added children:" + node.getChildren().size());
			val = minimaxAB(node, filter, depth-1, alpha, beta);
			return val;
		}
		else {
			logger.debug("simulating -- alpha: " + alpha + " beta: " + beta + " depth:" + depth + " step:" + game.getTurn().getStepType() + " for player:" + (node.getPlayerId().equals(playerId) ? "yes" : "no"));
			if (allPassed(game)) {
				if (!game.getStack().isEmpty()) {
					resolve(node, depth, game);
				}
				else {
					game.getPlayers().resetPassed();
					playNext(game, game.getActivePlayerId(), node);
				}
			}

			if (game.isGameOver()) {
				val = GameStateEvaluator2.evaluate(playerId, game);
			} else if (node.getChildren().size() > 0) {
				//declared attackers or blockers or triggered abilities
				logger.debug("simulating -- attack/block/trigger added children:" + node.getChildren().size());
				val = minimaxAB(node, filter, depth-1, alpha, beta);
			}
			else {
				val = simulatePriority(node, game, filter, depth, alpha, beta);
			}
		}

		logger.debug("returning -- score: " + val + " depth:" + depth + " step:" + game.getTurn().getStepType() + " for player:" + game.getPlayer(node.getPlayerId()).getName());
		return val;

	}

	protected int simulatePriority(SimulationNode2 node, Game game, FilterAbility filter, int depth, int alpha, int beta) {
		if (Thread.interrupted()) {
			Thread.currentThread().interrupt();
			logger.info("interrupted");
			return GameStateEvaluator2.evaluate(playerId, game);
		}
		node.setGameValue(game.getState().getValue());
		SimulatedPlayer2 currentPlayer = (SimulatedPlayer2) game.getPlayer(game.getPlayerList().get());
		//logger.info("simulating -- player " + currentPlayer.getName());
		SimulationNode2 bestNode = null;
		List<Ability> allActions = currentPlayer.simulatePriority(game, filter);
		logger.debug("simulating -- adding " + allActions.size() + " children:" + allActions);
		for (Ability action: allActions) {
			if (Thread.interrupted()) {
				Thread.currentThread().interrupt();
				logger.debug("interrupted");
				break;
			}
			Game sim = game.copy();
			if (sim.getPlayer(currentPlayer.getId()).activateAbility((ActivatedAbility) action.copy(), sim)) {
				sim.applyEffects();
				if (checkForRepeatedAction(sim, node, action, currentPlayer.getId()))
					continue;
				if (!sim.isGameOver() && action.isUsesStack()) {
					// only pass if the last action uses the stack
					sim.getPlayer(currentPlayer.getId()).pass();
					sim.getPlayerList().getNext();
				}
				SimulationNode2 newNode = new SimulationNode2(node, sim, action, depth, currentPlayer.getId());
				logger.debug("simulating -- node #:" + SimulationNode2.getCount() + " actions:" + action);
				sim.checkStateAndTriggered();
				if (depth == 20) {
					logger.info("*** Action *** " + action.toString());
				}
				int val = addActions(newNode, filter, depth-1, alpha, beta);
				if (depth == 20) {
					logger.info("*** Value *** " + val);
				}
				if (!currentPlayer.getId().equals(playerId)) {
					if (val < beta) {
						beta = val;
						bestNode = newNode;
						bestNode.setScore(val);
						node.setCombat(newNode.getCombat());
					}

					// no need to check other actions
					if (val == GameStateEvaluator2.LOSE_GAME_SCORE) {
						logger.debug("lose - break");
						break;
					}
				}
				else {
					if (val > alpha) {
						alpha = val;
						bestNode = newNode;
						bestNode.setScore(val);
						node.setCombat(newNode.getCombat());
						if (node.getTargets().size() > 0)
							targets = node.getTargets();
						if (node.getChoices().size() > 0)
							choices = node.getChoices();
						if (depth == maxDepth) {
							logger.info("saved");
							node.children.clear();
							node.children.add(bestNode);
							node.setScore(bestNode.getScore());
                        }
					}

					// no need to check other actions
					if (val == GameStateEvaluator2.WIN_GAME_SCORE) {
						logger.debug("win - break");
						break;
					}
				}
				if (alpha >= beta) {
					//logger.info("simulating -- pruning");
					break;
				}
				if (SimulationNode2.nodeCount > maxNodes) {
					logger.debug("simulating -- reached end-state");
					break;
				}
			}
		}
		if (bestNode != null) {
			node.children.clear();
			node.children.add(bestNode);
			node.setScore(bestNode.getScore());
		}
		if (!currentPlayer.getId().equals(playerId)) {
			//logger.info("returning priority beta: " + beta);
			return beta;
		}
		else {
			//logger.info("returning priority alpha: " + alpha);
			return alpha;
		}
	}

	protected boolean allPassed(Game game) {
		for (Player player: game.getPlayers().values()) {
			if (!player.isPassed() && !player.hasLost() && !player.hasLeft())
				return false;
		}
		return true;
	}

	@Override
	public boolean choose(Outcome outcome, Choice choice, Game game) {
		if (choices.size() == 0)
			return super.choose(outcome, choice, game);
		if (!choice.isChosen()) {
			for (String achoice: choices) {
				choice.setChoice(achoice);
				if (choice.isChosen()) {
					choices.clear();
					return true;
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean chooseTarget(Outcome outcome, Cards cards, TargetCard target, Ability source, Game game)  {
		if (targets.size() == 0)
			return super.chooseTarget(outcome, cards, target, source, game);
		if (!target.doneChosing()) {
			for (UUID targetId: targets) {
				target.addTarget(targetId, source, game);
				if (target.doneChosing()) {
					targets.clear();
					return true;
				}
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean choose(Outcome outcome, Cards cards, TargetCard target, Game game)  {
		if (targets.size() == 0)
			return super.choose(outcome, cards, target, game);
		if (!target.doneChosing()) {
			for (UUID targetId: targets) {
				target.add(targetId, game);
				if (target.doneChosing()) {
					targets.clear();
					return true;
				}
			}
			return false;
		}
		return true;
	}

		@Override
	public boolean playXMana(VariableManaCost cost, ManaCosts<ManaCost> costs, Game game) {
		//SimulatedPlayer.simulateVariableCosts method adds a generic mana cost for each option
		for (ManaCost manaCost: costs) {
			if (manaCost instanceof GenericManaCost) {
				cost.setPayment(manaCost.getPayment());
				logger.debug("using X = " + cost.getPayment().count());
				break;
			}
		}
		cost.setPaid();
		return true;
	}

	public void playNext(Game game, UUID activePlayerId, SimulationNode2 node) {
		boolean skip = false;
		while (true) {
			Phase currentPhase = game.getPhase();
			if (!skip)
				currentPhase.getStep().endStep(game, activePlayerId);
			game.applyEffects();
			switch (currentPhase.getStep().getType()) {
				case UNTAP:
					game.getPhase().setStep(new UpkeepStep());
					break;
				case UPKEEP:
					game.getPhase().setStep(new DrawStep());
					break;
				case DRAW:
					game.getTurn().setPhase(new PreCombatMainPhase());
					game.getPhase().setStep(new PreCombatMainStep());
					break;
				case PRECOMBAT_MAIN:
					game.getTurn().setPhase(new CombatPhase());
					game.getPhase().setStep(new BeginCombatStep());
					break;
				case BEGIN_COMBAT:
					game.getPhase().setStep(new DeclareAttackersStep());
					break;
				case DECLARE_ATTACKERS:
					game.getPhase().setStep(new DeclareBlockersStep());
					break;
				case DECLARE_BLOCKERS:
					game.getPhase().setStep(new CombatDamageStep(true));
					break;
				case COMBAT_DAMAGE:
					if (((CombatDamageStep)currentPhase.getStep()).getFirst())
						game.getPhase().setStep(new CombatDamageStep(false));
					else
						game.getPhase().setStep(new EndOfCombatStep());
					break;
				case END_COMBAT:
					game.getTurn().setPhase(new PostCombatMainPhase());
					game.getPhase().setStep(new PostCombatMainStep());
					break;
				case POSTCOMBAT_MAIN:
					game.getTurn().setPhase(new EndPhase());
					game.getPhase().setStep(new EndStep());
					break;
				case END_TURN:
					game.getPhase().setStep(new CleanupStep());
					break;
				case CLEANUP:
					game.getPhase().getStep().beginStep(game, activePlayerId);
					if (!game.checkStateAndTriggered() && !game.isGameOver()) {
						game.getState().setActivePlayerId(game.getState().getPlayerList(game.getActivePlayerId()).getNext());
						game.getTurn().setPhase(new BeginningPhase());
						game.getPhase().setStep(new UntapStep());
					}
			}
			if (!game.getStep().skipStep(game, game.getActivePlayerId())) {
				if (game.getTurn().getStepType() == PhaseStep.DECLARE_ATTACKERS) {
					game.fireEvent(new GameEvent(GameEvent.EventType.DECLARE_ATTACKERS_STEP_PRE, null, null, activePlayerId));
					if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.DECLARING_ATTACKERS, activePlayerId, activePlayerId))) {
						for (Combat engagement: ((SimulatedPlayer2)game.getPlayer(activePlayerId)).addAttackers(game)) {
							Game sim = game.copy();
							UUID defenderId = game.getOpponents(playerId).iterator().next();
							for (CombatGroup group: engagement.getGroups()) {
								for (UUID attackerId: group.getAttackers()) {
									sim.getPlayer(activePlayerId).declareAttacker(attackerId, defenderId, sim);
								}
							}
							sim.fireEvent(GameEvent.getEvent(GameEvent.EventType.DECLARED_ATTACKERS, playerId, playerId));
							SimulationNode2 newNode = new SimulationNode2(node, sim, node.getDepth()-1, activePlayerId);
							logger.debug("simulating -- node #:" + SimulationNode2.getCount() + " declare attakers");
							newNode.setCombat(sim.getCombat());
							node.children.add(newNode);
						}
					}
				}
				else if (game.getTurn().getStepType() == PhaseStep.DECLARE_BLOCKERS) {
					game.fireEvent(new GameEvent(GameEvent.EventType.DECLARE_BLOCKERS_STEP_PRE, null, null, activePlayerId));
					if (!game.replaceEvent(GameEvent.getEvent(GameEvent.EventType.DECLARING_BLOCKERS, activePlayerId, activePlayerId))) {
						for (UUID defenderId: game.getCombat().getDefenders()) {
							//check if defender is being attacked
							if (game.getCombat().isAttacked(defenderId, game)) {
								for (Combat engagement: ((SimulatedPlayer2)game.getPlayer(defenderId)).addBlockers(game)) {
									Game sim = game.copy();
									for (CombatGroup group: engagement.getGroups()) {
										List<UUID> blockers = new ArrayList<UUID>();
										blockers.addAll(group.getBlockers());
										for (UUID blockerId: blockers) {
											group.addBlocker(blockerId, defenderId, sim);
										}
										blockers = null;
									}
									sim.fireEvent(GameEvent.getEvent(GameEvent.EventType.DECLARED_BLOCKERS, playerId, playerId));
									SimulationNode2 newNode = new SimulationNode2(node, sim, node.getDepth()-1, defenderId);
									logger.debug("simulating -- node #:" + SimulationNode2.getCount() + " declare blockers");
									newNode.setCombat(sim.getCombat());
									node.children.add(newNode);
								}
							}
						}
					}
				}
				else {
					game.getStep().beginStep(game, activePlayerId);
				}
				if (game.getStep().getHasPriority())
					break;
			}
			else {
				skip = true;
			}
		}
		game.checkStateAndTriggered();
	}

	@Override
	public void selectAttackers(Game game) {
		logger.debug("selectAttackers");
		if (combat != null) {
			UUID opponentId = game.getCombat().getDefenders().iterator().next();
			String attackers = "";
			for (UUID attackerId: combat.getAttackers()) {
				Permanent attacker = game.getPermanent(attackerId);
				if (attacker != null) {
					attackers = "[" + attacker.getName() + "]";
					this.declareAttacker(attackerId, opponentId, game);
				}
			}
			logger.info("declare attackers: " + (attackers.isEmpty() ? "none" : attackers));
		}
	}

	@Override
	public void selectBlockers(Game game) {
		logger.debug("selectBlockers");
		if (combat != null && combat.getGroups().size() > 0) {
			List<CombatGroup> groups = game.getCombat().getGroups();
			for (int i = 0; i < groups.size(); i++) {
				if (i < combat.getGroups().size()) {
					for (UUID blockerId: combat.getGroups().get(i).getBlockers()) {
						this.declareBlocker(blockerId, groups.get(i).getAttackers().get(0), game);
					}
				}
			}
		}
	}

	/**
	 * Copies game and replaces all players in copy with simulated players
	 *
	 * @param game
	 * @return a new game object with simulated players
	 */
	protected Game createSimulation(Game game) {
		Game sim = game.copy();

		for (Player copyPlayer: sim.getState().getPlayers().values()) {
			Player origPlayer = game.getState().getPlayers().get(copyPlayer.getId());
			SimulatedPlayer2 newPlayer = new SimulatedPlayer2(copyPlayer.getId(), copyPlayer.getId().equals(playerId));
			newPlayer.restore(origPlayer);
			sim.getState().getPlayers().put(copyPlayer.getId(), newPlayer);
		}
		return sim;
	}

	private boolean checkForRepeatedAction(Game sim, SimulationNode2 node, Ability action, UUID playerId) {
		if (action instanceof PassAbility)
			return false;
		int val = GameStateEvaluator2.evaluate(playerId, sim);
		SimulationNode2 test = node.getParent();
		while (test != null && !test.getPlayerId().equals(playerId)) {
			test = test.getParent();
		}
		if (test != null && test.getAbilities() != null && test.getAbilities().size() == 1) {
			if (action.toString().equals(test.getAbilities().get(0).toString()) && GameStateEvaluator2.evaluate(playerId, sim) == val) {
				return true;
			}
		}
		return false;
	}
}
