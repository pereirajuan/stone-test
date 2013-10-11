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
package mage.sets.urzaslegacy;

import java.util.UUID;

import mage.constants.*;
import mage.abilities.Ability;
import mage.abilities.common.AsEntersBattlefieldAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continious.BoostAllEffect;
import mage.cards.CardImpl;
import mage.cards.repository.CardRepository;
import mage.choices.Choice;
import mage.choices.ChoiceImpl;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

/**
 *
 * @author Plopman
 */
public class EngineeredPlague extends CardImpl<EngineeredPlague> {

    public EngineeredPlague(UUID ownerId) {
        super(ownerId, 51, "Engineered Plague", Rarity.UNCOMMON, new CardType[]{CardType.ENCHANTMENT}, "{2}{B}");
        this.expansionSetCode = "ULG";

        this.color.setBlack(true);

        // As Engineered Plague enters the battlefield, choose a creature type.
        this.addAbility(new AsEntersBattlefieldAbility(new EngineeredPlagueEntersBattlefieldEffect(), "choose a creature type"));
        // All creatures of the chosen type get -1/-1.
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new BoostAllEffect(-1, -1, Duration.WhileOnBattlefield, new FilterEngineeredPlague(), false)));
    }

    public EngineeredPlague(final EngineeredPlague card) {
        super(card);
    }

    @Override
    public EngineeredPlague copy() {
        return new EngineeredPlague(this);
    }
    
    class EngineeredPlagueEntersBattlefieldEffect extends OneShotEffect<EngineeredPlagueEntersBattlefieldEffect> {

        public EngineeredPlagueEntersBattlefieldEffect() {
            super(Outcome.Benefit);
            staticText = "As {this} enters the battlefield, choose a creature type";
        }

        public EngineeredPlagueEntersBattlefieldEffect(final EngineeredPlagueEntersBattlefieldEffect effect) {
            super(effect);
        }

        @Override
        public boolean apply(Game game, Ability source) {
            Player player = game.getPlayer(source.getControllerId());
            Permanent permanent = game.getPermanent(source.getSourceId());
            if (player != null && permanent != null) {
                Choice typeChoice = new ChoiceImpl(true);
                typeChoice.setMessage("Choose creature type");
                typeChoice.setChoices(CardRepository.instance.getCreatureTypes());
                while (!player.choose(Outcome.Detriment, typeChoice, game) && player.isInGame()) {
                    game.debugMessage("player canceled choosing type. retrying.");
                }
                game.informPlayers(permanent.getName() + ": " + player.getName() + " has chosen " + typeChoice.getChoice());
                game.getState().setValue(permanent.getId() + "_type", typeChoice.getChoice().toString());
                permanent.addInfo("chosen type", "<i>Chosen type: " + typeChoice.getChoice() + "</i>");
            }
            return false;
        }

        @Override
        public EngineeredPlagueEntersBattlefieldEffect copy() {
            return new EngineeredPlagueEntersBattlefieldEffect(this);
        }

    }
    
    class FilterEngineeredPlague extends FilterCreaturePermanent {

        public FilterEngineeredPlague() {
            super("All creatures of the chosen type");
        }

        public FilterEngineeredPlague(final FilterEngineeredPlague filter) {
            super(filter);
        }

        @Override
        public FilterEngineeredPlague copy() {
            return new FilterEngineeredPlague(this);
        }

        @Override
        public boolean match(Permanent permanent, UUID sourceId, UUID playerId, Game game) {
            if(super.match(permanent, sourceId, playerId, game)){
                String subtype = (String) game.getState().getValue(sourceId + "_type");
                if(subtype != null && !subtype.equals("") && permanent.hasSubtype(subtype)){
                    return true;
                }
            }
            return false;
        }

           

    }

    
}

