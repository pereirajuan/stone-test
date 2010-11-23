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

package mage.abilities.effects;

import java.util.Date;
import mage.Constants.Duration;
import mage.Constants.EffectType;
import mage.Constants.Layer;
import mage.Constants.Outcome;
import mage.Constants.SubLayer;
import mage.abilities.Ability;
import mage.game.Game;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public abstract class ContinuousEffectImpl<T extends ContinuousEffectImpl<T>> extends EffectImpl<T> implements ContinuousEffect<T> {

	protected Duration duration;
	protected Layer layer;
	protected SubLayer sublayer;
	protected Date timestamp;
	protected boolean used = false;

	public ContinuousEffectImpl(Duration duration, Outcome outcome) {
		super(outcome);
		this.duration = duration;
		this.timestamp = new Date();
		this.effectType = EffectType.CONTINUOUS;
	}

	public ContinuousEffectImpl(Duration duration, Layer layer, SubLayer sublayer, Outcome outcome) {
		this(duration, outcome);
		this.layer = layer;
		this.sublayer = sublayer;
	}

	public ContinuousEffectImpl(final ContinuousEffectImpl effect) {
		super(effect);
		this.duration = effect.duration;
		this.layer = effect.layer;
		this.sublayer = effect.sublayer;
		this.timestamp = new Date(effect.timestamp.getTime());
		this.used = effect.used;
	}

	@Override
	public Duration getDuration() {
		return duration;
	}

	@Override
	public boolean apply(Layer layer, SubLayer sublayer, Ability source, Game game) {
		if (this.layer == layer && this.sublayer == sublayer) {
			return apply(game, source);
		}
		return false;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public void setTimestamp() {
		this.timestamp = new Date();
	}

	@Override
	public boolean hasLayer(Layer layer) {
		return this.layer == layer;
	}

	@Override
	public boolean isUsed() {
		return used;
	}

	@Override
	public void init(Ability source, Game game) {}

}
