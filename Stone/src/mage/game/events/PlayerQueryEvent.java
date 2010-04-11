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

package mage.game.events;

import java.io.Serializable;
import java.util.Collection;
import java.util.EventObject;
import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.ActivatedAbility;
import mage.abilities.TriggeredAbilities;
import mage.cards.Cards;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class PlayerQueryEvent extends EventObject implements ExternalEvent, Serializable {

	public enum QueryType {
		ASK, CHOOSE, CHOOSE_ABILITY, PICK_TARGET, PICK_ABILITY, SELECT, PLAY_MANA, PLAY_X_MANA, AMOUNT
	}

	private String message;
	private Collection<? extends Ability> abilities;
	private String[] choices;
	private Cards cards;
	private QueryType queryType;
	private UUID playerId;
	private boolean required;
	private int min;
	private int max;

	private PlayerQueryEvent(UUID playerId, String message, Collection<? extends Ability> abilities, String[] choices, Cards cards, QueryType queryType, int min, int max, boolean required) {
		super(playerId);
		this.queryType = queryType;
		this.message = message;
		this.playerId = playerId;
		this.abilities = abilities;
		this.choices = choices;
		this.cards = cards;
		this.required = required;
		this.min = min;
		this.max = max;
	}

	public static PlayerQueryEvent askEvent(UUID playerId, String message) {
		return new PlayerQueryEvent(playerId, message, null, null, null, QueryType.ASK, 0, 0, false);
	}
	
	public static PlayerQueryEvent chooseAbilityEvent(UUID playerId, String message, Collection<? extends ActivatedAbility> choices) {
		return new PlayerQueryEvent(playerId, message, choices, null, null, QueryType.CHOOSE_ABILITY, 0, 0, false);
	}
	public static PlayerQueryEvent chooseEvent(UUID playerId, String message, String[] choices) {
		return new PlayerQueryEvent(playerId, message, null, choices, null, QueryType.CHOOSE, 0, 0, false);
	}

	public static PlayerQueryEvent targetEvent(UUID playerId, String message, boolean required) {
		return new PlayerQueryEvent(playerId, message, null, null, null, QueryType.PICK_TARGET, 0, 0, required);
	}

	public static PlayerQueryEvent targetEvent(UUID playerId, String message, Cards cards, boolean required) {
		return new PlayerQueryEvent(playerId, message, null, null, cards, QueryType.PICK_TARGET, 0, 0, required);
	}

	public static PlayerQueryEvent targetEvent(UUID playerId, String message, TriggeredAbilities abilities, boolean required) {
		return new PlayerQueryEvent(playerId, message, abilities, null, null, QueryType.PICK_ABILITY, 0, 0, required);
	}

	public static PlayerQueryEvent selectEvent(UUID playerId, String message) {
		return new PlayerQueryEvent(playerId, message, null, null, null, QueryType.SELECT, 0, 0, false);
	}

	public static PlayerQueryEvent playManaEvent(UUID playerId, String message) {
		return new PlayerQueryEvent(playerId, message, null, null, null, QueryType.PLAY_MANA, 0, 0, false);
	}

	public static PlayerQueryEvent playXManaEvent(UUID playerId, String message) {
		return new PlayerQueryEvent(playerId, message, null, null, null, QueryType.PLAY_X_MANA, 0, 0, false);
	}

	public static PlayerQueryEvent amountEvent(UUID playerId, String message, int min , int max) {
		return new PlayerQueryEvent(playerId, message, null, null, null, QueryType.AMOUNT, min, max, false);
	}

	public String getMessage() {
		return message;
	}

	public QueryType getQueryType() {
		return queryType;
	}

	public Collection<? extends Ability> getAbilities() {
		return abilities;
	}

	public String[] getChoices() {
		return choices;
	}

	public UUID getPlayerId() {
		return playerId;
	}

	public boolean isRequired() {
		return required;
	}

	public Cards getCards() {
		return cards;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}
}
