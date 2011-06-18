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

package mage.sets.mirrodinbesieged;

import java.util.UUID;

import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.common.EntersBattlefieldAbility;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.EntersBattlefieldEffect;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.sets.magic2011.ThunderStrike;
import mage.target.common.TargetCreaturePermanent;

/**
 * @author Loki
 */
public class Cryptoplasm extends CardImpl<Cryptoplasm> {

    public Cryptoplasm(UUID ownerId) {
        super(ownerId, 23, "Cryptoplasm", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}{U}{U}");
        this.expansionSetCode = "MBS";
        this.subtype.add("Shapeshifter");
        this.color.setBlue(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);
        Ability ability = new BeginningOfUpkeepTriggeredAbility(new CryptoplasmTransformEffect(), Constants.TargetController.YOU, true);
        ability.addTarget(new TargetCreaturePermanent());
        this.addAbility(ability);
    }

    public Cryptoplasm(final Cryptoplasm card) {
        super(card);
    }

    @Override
    public Cryptoplasm copy() {
        return new Cryptoplasm(this);
    }

}

class CryptoplasmTransformEffect extends ContinuousEffectImpl<CryptoplasmTransformEffect> {

    CryptoplasmTransformEffect() {
        super(Constants.Duration.WhileOnBattlefield, Constants.Layer.CopyEffects_1, Constants.SubLayer.NA, Constants.Outcome.BecomeCreature);
    }

    CryptoplasmTransformEffect(final CryptoplasmTransformEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card card = game.getCard(targetPointer.getFirst(source));
        Permanent permanent = game.getPermanent(source.getSourceId());

        if (card == null || permanent == null)
            return true;

        permanent.setName(card.getName());
        permanent.getColor().setColor(card.getColor());
        permanent.getManaCost().clear();
        permanent.getManaCost().add(card.getManaCost());
        permanent.getCardType().clear();
        for (CardType type : card.getCardType()) {
            permanent.getCardType().add(type);
        }
        permanent.getSubtype().clear();
        for (String type : card.getSubtype()) {
            permanent.getSubtype().add(type);
        }
        permanent.getSupertype().clear();
        for (String type : card.getSupertype()) {
            permanent.getSupertype().add(type);
        }
        permanent.setExpansionSetCode(card.getExpansionSetCode());
        permanent.getAbilities().clear();

        Ability upkeepAbility = new BeginningOfUpkeepTriggeredAbility(new CryptoplasmTransformEffect(), Constants.TargetController.YOU, true);
        upkeepAbility.addTarget(new TargetCreaturePermanent());
        permanent.addAbility(upkeepAbility);

        for (Ability ability : card.getAbilities()) {
            permanent.addAbility(ability);
        }

        permanent.getPower().setValue(card.getPower().getValue());
		permanent.getToughness().setValue(card.getToughness().getValue());

        return true;
    }

    @Override
    public CryptoplasmTransformEffect copy() {
        return new CryptoplasmTransformEffect(this);
    }

    @Override
    public String getText(Ability source) {
        return "you may have Cryptoplasm become a copy of another target creature. If you do, Cryptoplasm gains this ability";
    }
}