/*
* Copyright 2010 BetaSteward_at_googlemail.com. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are
* permitted provided that the following conditions are met:
*
*    1. Redistributions of source code must retain the above copyright notice, this list of
*       conditions and the following disclaimer.
*
*    2. Redistributions in binary form must reproduce the above copyright notice, this list
*       of conditions and the following disclaimer in the documentation and/or other materials
*       provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY BetaSteward_at_googlemail.com ``AS IS'' AND ANY EXPRESS OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
* FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BetaSteward_at_googlemail.com OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
* ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
* The views and conclusions contained in the software and documentation are those of the
* authors and should not be interpreted as representing official policies, either expressed
* or implied, of BetaSteward_at_googlemail.com.
*/

package mage.game.permanent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import mage.Constants.CardType;
import mage.Constants.Zone;
import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbility;
import mage.abilities.common.ZoneChangeTriggeredAbility;
import mage.abilities.keyword.LevelAbility;
import mage.cards.Card;
import mage.cards.LevelerCard;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ZoneChangeEvent;
import mage.players.Player;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class PermanentCard extends PermanentImpl<PermanentCard> {

	protected String art;
	protected List<String> levelerRules;

	public PermanentCard(Card card, UUID controllerId) {
		super(card.getId(), card.getOwnerId(), controllerId, card.getName());
		init(card);
	}

	protected PermanentCard(UUID id, Card card, UUID controllerId) {
		super(card.getId(), card.getOwnerId(), controllerId, card.getName());
		init(card);
	}

	protected void init(Card card) {
		copyFromCard(card);
		if (card.getCardType().contains(CardType.PLANESWALKER)) {
			this.loyalty = new MageInt(card.getLoyalty().getValue());
		}
		if (card instanceof LevelerCard) {
			levelerRules = ((LevelerCard)card).getRules();
		}
	}

	public PermanentCard(final PermanentCard permanent) {
		super(permanent);
		this.art = permanent.art;
	}

	@Override
	public void reset(Game game) {
		// when the permanent is reset copy all original values from the card
		// must copy card each reset so that the original values don't get modified
//		Card copy = game.getCard(objectId).copy();
		copyFromCard(game.getCard(objectId));
		super.reset(game);
	}

	protected void copyFromCard(Card card) {
		this.name = card.getName();
		this.manaCost = card.getManaCost().copy();
		this.color = card.getColor().copy();
		this.power = card.getPower().copy();
		this.toughness = card.getToughness().copy();
		this.loyalty = card.getLoyalty().copy();
		this.abilities = card.getAbilities().copy();
		this.abilities.setControllerId(controllerId);
		this.cardType.clear();
		for (CardType cType: card.getCardType()) {
			this.cardType.add(cType);
		}
		this.subtype.clear();
		for (String subType: card.getSubtype()) {
			this.subtype.add(subType);
		}
		this.supertype.clear();
		for (String superType: card.getSupertype()) {
			this.supertype.add(superType);
		}
		if (card instanceof LevelerCard) {
			LevelAbility level = ((LevelerCard)card).getLevel(this.getCounters().getCount("Level"));
			if (level != null) {
				this.power.setValue(level.getPower());
				this.toughness.setValue(level.getToughness());
				for (Ability ability: level.getAbilities()) {
					this.addAbility(ability);
				}
			}
		}
		this.art = card.getArt();
		this.expansionSetCode = card.getExpansionSetCode();
		this.rarity = card.getRarity();
		this.cardNumber = card.getCardNumber();
	}

	public void checkPermanentOnlyTriggers(ZoneChangeEvent event, Game game) {
		// we only want to trigger abilities that are not on the underlying card ie. have been added by another effect
		// or we want to trigger abilities that only trigger on leaving the battlefield
		// card abilities will get triggered later when the card hits the new zone
		Card card = game.getCard(objectId).copy();
		for (TriggeredAbility ability: abilities.getTriggeredAbilities(event.getFromZone())) {
			if (!card.getAbilities().containsKey(ability.getId())) {
				if (ability.checkTrigger(event, game)) {
					ability.trigger(game, controllerId);
				}
			} else if (ability instanceof ZoneChangeTriggeredAbility && event.getFromZone() == Zone.BATTLEFIELD) {
				ZoneChangeTriggeredAbility zcAbility = (ZoneChangeTriggeredAbility)ability;
				if (zcAbility.getToZone() == null) {
					if (ability.checkTrigger(event, game)) {
						ability.trigger(game, controllerId);
					}
				}
			}
		}
		for (TriggeredAbility ability: abilities.getTriggeredAbilities(event.getToZone())) {
			if (!card.getAbilities().containsKey(ability.getId())) {
				if (ability.checkTrigger(event, game)) {
					ability.trigger(game, controllerId);
				}
			}
		}
	}


	@Override
	public boolean moveToZone(Zone toZone, UUID sourceId, Game game, boolean flag) {
		Zone fromZone = game.getZone(objectId);
		Player controller = game.getPlayer(controllerId);
		if (controller != null && controller.removeFromBattlefield(this, game)) {
			ZoneChangeEvent event = new ZoneChangeEvent(this, sourceId, controllerId, fromZone, toZone);
			if (!game.replaceEvent(event)) {
				Card card = game.getCard(objectId);
				Player owner = game.getPlayer(ownerId);
				game.rememberLKI(objectId, Zone.BATTLEFIELD, this);
				if (owner != null) {
					switch (event.getToZone()) {
						case GRAVEYARD:
							owner.putInGraveyard(card, game, !flag);
							break;
						case HAND:
							owner.getHand().add(card);
							break;
						case EXILED:
							game.getExile().getPermanentExile().add(card);
							break;
						case LIBRARY:
							if (flag)
								owner.getLibrary().putOnTop(card, game);
							else
								owner.getLibrary().putOnBottom(card, game);
							break;
						case BATTLEFIELD:
							//should never happen
							break;
					}
					game.setZone(objectId, event.getToZone());
					game.fireEvent(event);
					return game.getZone(objectId) == toZone;
				}
			}
		}
		return false;
	}


	@Override
	public boolean moveToExile(UUID exileId, String name, UUID sourceId, Game game) {
		Zone fromZone = game.getZone(objectId);
		Player controller = game.getPlayer(controllerId);
		if (controller != null && controller.removeFromBattlefield(this, game)) {
			ZoneChangeEvent event = new ZoneChangeEvent(this, sourceId, ownerId, fromZone, Zone.EXILED);
			if (!game.replaceEvent(event)) {
				Card card = game.getCard(this.objectId);
				if (exileId == null) {
					game.getExile().getPermanentExile().add(card);
				}
				else {
					game.getExile().createZone(exileId, name).add(card);
				}
				game.setZone(objectId, event.getToZone());
				game.fireEvent(event);
				return true;
			}
		}
		return false;
	}

	@Override
	public String getArt() {
		return art;
	}

	@Override
	public void setArt(String art) {
		this.art = art;
	}

	@Override
	public PermanentCard copy() {
		return new PermanentCard(this);
	}

	@Override
	public List<String> getRules() {
		if (levelerRules == null)
			return super.getRules();
		List<String> rules = new ArrayList<String>();
		rules.addAll(super.getRules());
		rules.addAll(levelerRules);
		return rules;
	}

}
