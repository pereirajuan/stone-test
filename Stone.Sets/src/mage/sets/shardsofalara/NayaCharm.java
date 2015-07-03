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
package mage.sets.shardsofalara;

import java.util.List;
import java.util.UUID;
import mage.abilities.Mode;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.ReturnToHandTargetEffect;
import mage.abilities.effects.common.TapAllTargetPlayerControlsEffect;
import mage.cards.CardImpl;
import mage.constants.CardType;
import mage.constants.Rarity;
import mage.filter.common.FilterCreaturePermanent;
import mage.target.TargetPlayer;
import mage.target.common.TargetCardInGraveyard;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author North
 */
public class NayaCharm extends CardImpl {

    public NayaCharm(UUID ownerId) {
        super(ownerId, 180, "Naya Charm", Rarity.UNCOMMON, new CardType[]{CardType.INSTANT}, "{R}{G}{W}");
        this.expansionSetCode = "ALA";

        // Choose one - Naya Charm deals 3 damage to target creature;
        this.getSpellAbility().addEffect(new DamageTargetEffect(3));
        this.getSpellAbility().addTarget(new TargetCreaturePermanent());
        // or return target card from a graveyard to its owner's hand;
        Mode mode = new Mode();
        mode.getEffects().add(new ReturnToHandTargetEffect());
        mode.getTargets().add(new TargetCardInGraveyard());
        this.getSpellAbility().addMode(mode);
        // or tap all creatures target player controls.
        mode = new Mode();
        mode.getEffects().add(new TapAllTargetPlayerControlsEffect(new FilterCreaturePermanent("creatures")));
        mode.getTargets().add(new TargetPlayer());
        this.getSpellAbility().addMode(mode);
    }

    public NayaCharm(final NayaCharm card) {
        super(card);
    }

    @Override
    public NayaCharm copy() {
        return new NayaCharm(this);
    }
}
