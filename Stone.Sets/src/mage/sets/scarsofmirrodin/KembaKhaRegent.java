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

package mage.sets.scarsofmirrodin;

import java.util.UUID;

import mage.Constants;
import mage.Constants.CardType;
import mage.Constants.Rarity;
import mage.MageInt;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.abilities.common.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.game.permanent.token.Token;

/**
 *
 * @author Loki
 */
public class KembaKhaRegent extends CardImpl<KembaKhaRegent> {

    public KembaKhaRegent (UUID ownerId) {
        super(ownerId, 12, "Kemba, Kha Regent", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{1}{W}{W}");
        this.expansionSetCode = "SOM";
        this.subtype.add("Legendary");
        this.subtype.add("Cat");
        this.subtype.add("Cleric");
		this.color.setWhite(true);
        this.power = new MageInt(2);
        this.toughness = new MageInt(4);
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(new KembaKhaRegentEffect(), Constants.TargetController.YOU, false));
    }

    public KembaKhaRegent (final KembaKhaRegent card) {
        super(card);
    }

    @Override
    public KembaKhaRegent copy() {
        return new KembaKhaRegent(this);
    }
}

class KembaKhaRegentEffect extends OneShotEffect<KembaKhaRegentEffect> {
    public KembaKhaRegentEffect() {
        super(Constants.Outcome.PutCreatureInPlay);
    }

    public KembaKhaRegentEffect(final KembaKhaRegentEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent p = game.getPermanent(source.getSourceId());
        int count = 0;
        if (p != null) {
            for (UUID attachmentId : p.getAttachments()) {
                Permanent attached = game.getPermanent(attachmentId);
                if (attached != null && attached.getSubtype().contains("Equipment"))
                    count++;
            }
            if (count > 0) {
                CatToken token = new CatToken();
                for (int i = 0; i < count; i++) {
			        token.putOntoBattlefield(game, source.getId(), source.getControllerId());
		        }
            }

        }
        return false;
    }

    @Override
    public KembaKhaRegentEffect copy() {
        return new KembaKhaRegentEffect(this);
    }

    @Override
    public String getText(Ability source) {
        return "put a 2/2 white Cat creature token onto the battlefield for each Equipment attached to Kemba, Kha Regent";
    }
}

class CatToken extends Token {
    public CatToken() {
        super("Cat", "a 2/2 white Cat creature token");
        cardType.add(CardType.CREATURE);
		color = ObjectColor.WHITE;
		subtype.add("Cat");
		power = new MageInt(2);
		toughness = new MageInt(2);
    }
}