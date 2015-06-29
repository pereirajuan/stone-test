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

package mage.sets.returntoravnica;

import java.util.UUID;
import mage.MageObject;
import mage.constants.AttachmentType;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.Cost;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.AttachEffect;
import mage.abilities.effects.common.continuous.GainAbilityAttachedEffect;
import mage.abilities.keyword.EnchantAbility;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.target.common.TargetNonlandPermanent;

/**
 *
 * @author LevelX2
 */
public class SoulTithe extends CardImpl {

    static final String rule = "At the beginning of the upkeep of enchanted permanent's controller, that player sacrifices it unless he or she pays {X}, where X is its converted mana cost";

    public SoulTithe (UUID ownerId) {
        super(ownerId, 23, "Soul Tithe", Rarity.UNCOMMON, new CardType[]{CardType.ENCHANTMENT}, "{1}{W}");
        this.expansionSetCode = "RTR";
        this.subtype.add("Aura");

        // Enchant nonland permanent
        TargetPermanent auraTarget = new TargetNonlandPermanent();
        this.getSpellAbility().addTarget(auraTarget);
        this.getSpellAbility().addEffect(new AttachEffect(Outcome.Detriment));
        Ability ability = new EnchantAbility(auraTarget.getTargetName());
        this.addAbility(ability);

        // At the beginning of the upkeep of enchanted permanent's controller,
        // that player sacrifices it unless he or she pays {X},
        // where X is its converted mana cost.
        Ability gainedAbility = new BeginningOfUpkeepTriggeredAbility(new SoulTitheSacrificeSourceUnlessPaysEffect(), TargetController.YOU, false);
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new GainAbilityAttachedEffect(gainedAbility, AttachmentType.AURA, Duration.WhileOnBattlefield, rule)));

    }

    public SoulTithe (final SoulTithe card) {
        super(card);
    }

    @Override
    public SoulTithe copy() {
        return new SoulTithe(this);
    }
}

class SoulTitheSacrificeSourceUnlessPaysEffect extends OneShotEffect {

    public SoulTitheSacrificeSourceUnlessPaysEffect() {
        super(Outcome.Sacrifice);
        staticText = "that player sacrifices it unless he or she pays {X}, where X is its converted mana cost";
     }

    public SoulTitheSacrificeSourceUnlessPaysEffect(final SoulTitheSacrificeSourceUnlessPaysEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        Permanent permanent = game.getPermanent(source.getSourceId());
        MageObject sourceObject = source.getSourceObject(game);
        if (player != null && permanent != null && sourceObject != null) {
            int cmc = permanent.getManaCost().convertedManaCost();
            if (player.chooseUse(Outcome.Benefit, "Pay {" + cmc + "} for " + permanent.getName() + "? (otherwise you sacrifice it)", source, game)) {
                Cost cost = new GenericManaCost(cmc);
                if (cost.pay(source, game, source.getSourceId(), source.getControllerId(), false)) {
                    return true;
                }
            }
            permanent.sacrifice(source.getSourceId(), game);
            return true;
        }
        return false;
    }

    @Override
    public SoulTitheSacrificeSourceUnlessPaysEffect copy() {
        return new SoulTitheSacrificeSourceUnlessPaysEffect(this);
    }
 }
