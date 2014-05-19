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
package mage.sets.mirage;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.effects.common.continious.GainControlTargetEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.players.PlayerList;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author Quercitron
 */
public class IllicitAuction extends CardImpl<IllicitAuction> {

    public IllicitAuction(UUID ownerId) {
        super(ownerId, 183, "Illicit Auction", Rarity.RARE, new CardType[]{CardType.SORCERY}, "{3}{R}{R}");
        this.expansionSetCode = "MIR";

        this.color.setRed(true);

        // Each player may bid life for control of target creature. You start the bidding with a bid of 0. In turn order, each player may top the high bid. The bidding ends if the high bid stands. The high bidder loses life equal to the high bid and gains control of the creature.
        this.getSpellAbility().addEffect(new IllicitAuctionEffect());
        this.getSpellAbility().addTarget(new TargetCreaturePermanent(true));
    }

    public IllicitAuction(final IllicitAuction card) {
        super(card);
    }

    @Override
    public IllicitAuction copy() {
        return new IllicitAuction(this);
    }
}

// effect is based on ExchangeControlTargetEffect
class IllicitAuctionEffect extends GainControlTargetEffect {
    
    public IllicitAuctionEffect() {
        super(Duration.EndOfGame);
        this.staticText = "Each player may bid life for control of target creature. You start the bidding with a bid of 0. In turn order, each player may top the high bid. The bidding ends if the high bid stands. The high bidder loses life equal to the high bid and gains control of the creature.";
    }
    
    public IllicitAuctionEffect(final IllicitAuctionEffect effect) {
        super(effect);
    }

    @Override
    public IllicitAuctionEffect copy() {
        return new IllicitAuctionEffect(this);
    }
    
    @Override
    public void init(Ability source, Game game) {
        Permanent targetCreature = game.getPermanent(source.getFirstTarget());
        if (targetCreature != null) {
            PlayerList playerList = game.getPlayerList().copy();
            playerList.setCurrent(game.getActivePlayerId());
            
            Player winner = game.getPlayer(game.getActivePlayerId());
            int highBid = 0;
            game.informPlayers(new StringBuilder(winner.getName()).append(" bet 0 lifes").toString());
            
            Player currentPlayer = playerList.getNext(game);
            while (currentPlayer != winner) {
                String text = new StringBuilder(winner.getName()).append(" bet ").append(highBid).append(" life")
                        .append(highBid > 1 ? "s" : "").append(". Top the bid?").toString();
                if (currentPlayer.chooseUse(Outcome.GainControl, text, game)) {
                    int newBid = currentPlayer.getAmount(highBid + 1, Integer.MAX_VALUE, "Choose bid", game);
                    if (newBid > highBid) {
                        highBid = newBid;
                        winner = currentPlayer;
                        game.informPlayers(new StringBuilder(currentPlayer.getName()).append(" bet ")
                                .append(newBid).append(" life").append(newBid > 1 ? "s" : "").toString());
                    }
                }
                currentPlayer = playerList.getNext(game);
            }
            
            game.informPlayers(new StringBuilder(winner.getName()).append(" won auction with a bid of ").append(highBid).append(" life")
                    .append(highBid > 1 ? "s" : "").toString());
            winner.loseLife(highBid, game);
            super.controllingPlayerId = winner.getId();
        }
        
        super.init(source, game);
    }
    
}
