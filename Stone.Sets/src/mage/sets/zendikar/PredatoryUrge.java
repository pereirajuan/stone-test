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
package mage.sets.zendikar;

import java.util.UUID;
import mage.constants.AttachmentType;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.Zone;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.AttachEffect;
import mage.abilities.effects.common.continuous.GainAbilityAttachedEffect;
import mage.abilities.keyword.EnchantAbility;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author North
 */
public class PredatoryUrge extends CardImpl {

    public PredatoryUrge(UUID ownerId) {
        super(ownerId, 175, "Predatory Urge", Rarity.RARE, new CardType[]{CardType.ENCHANTMENT}, "{3}{G}");
        this.expansionSetCode = "ZEN";
        this.subtype.add("Aura");


        // Enchant creature
        TargetPermanent auraTarget = new TargetCreaturePermanent();
        this.getSpellAbility().addTarget(auraTarget);
        this.getSpellAbility().addEffect(new AttachEffect(Outcome.BoostCreature));
        Ability ability = new EnchantAbility(auraTarget.getTargetName());
        this.addAbility(ability);
        // Enchanted creature has "{tap}: This creature deals damage equal to its power to target creature.
        // That creature deals damage equal to its power to this creature."
        ability = new SimpleActivatedAbility(Zone.BATTLEFIELD, new PredatoryUrgeEffect(), new TapSourceCost());
        ability.addTarget(new TargetCreaturePermanent());
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new GainAbilityAttachedEffect(ability, AttachmentType.AURA)));
    }

    public PredatoryUrge(final PredatoryUrge card) {
        super(card);
    }

    @Override
    public PredatoryUrge copy() {
        return new PredatoryUrge(this);
    }
}

class PredatoryUrgeEffect extends OneShotEffect {

    public PredatoryUrgeEffect() {
        super(Outcome.Damage);
        this.staticText = "This creature deals damage equal to its power to target creature. That creature deals damage equal to its power to this creature.";
    }

    public PredatoryUrgeEffect(final PredatoryUrgeEffect effect) {
        super(effect);
    }

    @Override
    public PredatoryUrgeEffect copy() {
        return new PredatoryUrgeEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        boolean sourceOnBattlefield = true;
        Permanent targetCreature = game.getPermanent(source.getFirstTarget());
        Permanent sourceCreature = game.getPermanent(source.getSourceId());
        if (sourceCreature == null) {
            sourceCreature = (Permanent) game.getLastKnownInformation(source.getSourceId(), Zone.BATTLEFIELD);
            sourceOnBattlefield = false;
        }

        if (sourceCreature != null && targetCreature != null
                && sourceCreature.getCardType().contains(CardType.CREATURE)
                && targetCreature.getCardType().contains(CardType.CREATURE)) {
            targetCreature.damage(sourceCreature.getPower().getValue(), sourceCreature.getId(), game, false, true);
            if (sourceOnBattlefield) {
                sourceCreature.damage(targetCreature.getPower().getValue(), targetCreature.getId(), game, false, true);
            }
            return true;
        }
        return false;
    }
}
