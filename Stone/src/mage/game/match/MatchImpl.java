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

package mage.game.match;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import mage.cards.decks.Deck;
import mage.game.Game;
import mage.game.GameException;
import mage.game.events.Listener;
import mage.game.events.TableEvent;
import mage.game.events.TableEvent.EventType;
import mage.game.events.TableEventSource;
import mage.players.Player;


/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public abstract class MatchImpl implements Match {

    protected UUID id = UUID.randomUUID();
    protected List<MatchPlayer> players = new ArrayList<MatchPlayer>();
    protected List<Game> games = new ArrayList<Game>();
    protected MatchOptions options;

    protected TableEventSource tableEventSource = new TableEventSource();

    protected Date startTime;
    protected Date endTime;

    public MatchImpl(MatchOptions options) {
        this.options = options;
        startTime = new Date();
    }

    @Override
    public List<MatchPlayer> getPlayers() {
        return players;
    }

    @Override
    public MatchPlayer getPlayer(UUID playerId) {
        for (MatchPlayer player: players) {
            if (player.getPlayer().getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public void addPlayer(Player player, Deck deck) {
        MatchPlayer mPlayer = new MatchPlayer(player, deck);
        players.add(mPlayer);
    }

    @Override
    public boolean leave(UUID playerId) {
        MatchPlayer mPlayer = getPlayer(playerId);
        if (mPlayer != null) {
            if (games.isEmpty() ) {
                return players.remove(mPlayer);
            }
            mPlayer.setQuit(true);
            synchronized (this) {
                this.notifyAll();
            }
            return true;
        }
        return false;
    }

    @Override
    public void startMatch() throws GameException {
        this.startTime = new Date();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return options.getName();
    }

    @Override
    public MatchOptions getOptions() {
        return options;
    }

    @Override
    public boolean isMatchOver() {
        int activePlayers = 0;
        for (MatchPlayer player: players) {
            if (!player.hasQuit()) {
                activePlayers++;
            }
            if (player.getWins() >= options.getWinsNeeded()) {
                endTime = new Date();
                return true;
            }
        }
        if (activePlayers < 2) {
                endTime = new Date();
                return true;
        }
        return false;
    }

    @Override
    public Game getGame() {
        if (games.isEmpty()) {
            return null;
        }
        return games.get(games.size() -1);
    }

    @Override
    public List<Game> getGames() {
        return games;
    }

    @Override
    public int getNumGames() {
        return games.size();
    }

    @Override
    public int getWinsNeeded() {
        return options.getWinsNeeded();
    }

    @Override
    public int getFreeMulligans() {
        return options.getFreeMulligans();
    }

    protected void initGame(Game game) throws GameException {
        shufflePlayers();
        for (MatchPlayer matchPlayer: this.players) {
            if (!matchPlayer.hasQuit()) {
                matchPlayer.getPlayer().init(game);
                game.loadCards(matchPlayer.getDeck().getCards(), matchPlayer.getPlayer().getId());
                game.loadCards(matchPlayer.getDeck().getSideboard(), matchPlayer.getPlayer().getId());
                game.addPlayer(matchPlayer.getPlayer(), matchPlayer.getDeck());
                // set the priority time left for the match
                if (matchPlayer.getPriorityTimeLeft() > 0) {
                    matchPlayer.getPlayer().setPriorityTimeLeft(matchPlayer.getPriorityTimeLeft());
                }
            }
        }
        game.setPriorityTime(options.getPriorityTime());
    }

    protected void shufflePlayers() {
        Collections.shuffle(this.players, new Random());
    }

    @Override
    public void endGame() {
        Game game = getGame();
        for (MatchPlayer player: this.players) {
            Player p = game.getPlayer(player.getPlayer().getId());
            if (p != null) {
                // get the left time from player priority timer
                if (game.getPriorityTime() > 0) {
                    player.setPriorityTimeLeft(p.getPriorityTimeLeft());
                }
                if (p.hasQuit()) {
                    player.setQuit(true);
                }
                if (p.hasWon()) {
                    player.addWin();
                }
                if (p.hasLost()) {
                    player.addLose();
                }
            }
        }
        game.fireGameEndInfo();
    }

    @Override
    public UUID getChooser() {
        UUID loserId = null;
        Game game = getGame();
        for (MatchPlayer player: this.players) {
            Player p = game.getPlayer(player.getPlayer().getId());
            if (p != null && p.hasLost() && !p.hasQuit()) {
                loserId = p.getId();
            }
        }
        return loserId;
    }

    @Override
    public void addTableEventListener(Listener<TableEvent> listener) {
        tableEventSource.addListener(listener);
    }

    @Override
    public void sideboard() {
        for (MatchPlayer player: this.players) {
            if (!player.hasQuit()) {
                player.setSideboarding();
                player.getPlayer().sideboard(this, player.getDeck());
            }
        }
        synchronized(this) {
            while (!isDoneSideboarding()) {
                try {
                    this.wait();
                } catch (InterruptedException ex) { }
            }
        }
    }

    @Override
    public boolean isDoneSideboarding() {
        for (MatchPlayer player: this.players) {
            if (!player.hasQuit() && !player.isDoneSideboarding()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void fireSideboardEvent(UUID playerId, Deck deck) {
        MatchPlayer player = getPlayer(playerId);
        if (player != null) {
            tableEventSource.fireTableEvent(EventType.SIDEBOARD, playerId, deck, SIDEBOARD_TIME);
        }
    }

    @Override
    public void submitDeck(UUID playerId, Deck deck) {
        MatchPlayer player = getPlayer(playerId);
        if (player != null) {
            player.submitDeck(deck);
        }
        synchronized (this) {
            this.notifyAll();
        }
    }

    @Override
    public void updateDeck(UUID playerId, Deck deck) {
        MatchPlayer player = getPlayer(playerId);
        if (player != null) {
            player.updateDeck(deck);
        }
    }

    protected String createGameStartMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nMatch score:\n");
        for (MatchPlayer mp :this.getPlayers()) {
            sb.append("- ").append(mp.getPlayer().getName());
            sb.append(" (").append(mp.getWins()).append(mp.getWins()==1?" win / ":" wins / ");
            sb.append(mp.getLoses()).append(mp.getLoses()==1?" loss)":" losses)");
            if (mp.hasQuit()) {
                sb.append(" QUITTED");
            }
            sb.append("\n");
        }
        sb.append("\n").append(this.getWinsNeeded()).append(this.getWinsNeeded() == 1 ? " win":" wins").append(" needed to win the match\n");
        sb.append("\nGame has started\n");
        return sb.toString();
    }
    
    @Override
    public Date getStartTime() {
        return new Date(startTime.getTime());
    }

    @Override
    public Date getEndTime() {
        if (endTime != null) {
            return new Date(endTime.getTime());
        }
        return null;
    }

}
