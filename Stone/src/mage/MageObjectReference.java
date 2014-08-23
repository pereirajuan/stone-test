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

package mage;

import java.util.UUID;
import mage.cards.Card;
import mage.game.Game;
import mage.game.permanent.Permanent;

/**
 * A object reference that takes zone changes into account.
 *
 * @author LevelX2
 */

public class MageObjectReference implements Comparable<MageObjectReference> {

    private final UUID sourceId;
    private final int zoneChangeCounter;

    public MageObjectReference(Permanent permanent) {
        this.sourceId = permanent.getId();
        this.zoneChangeCounter = permanent.getZoneChangeCounter();
    }

    public MageObjectReference(Card card) {
        this.sourceId = card.getId();
        this.zoneChangeCounter = card.getZoneChangeCounter();
    }

    public MageObjectReference(UUID sourceId, Game game) {
        MageObject mageObject = game.getObject(sourceId);
        this.sourceId = sourceId;
        if (mageObject instanceof Permanent) {
            this.zoneChangeCounter = ((Permanent)mageObject).getZoneChangeCounter();
        } else if (mageObject instanceof Card) {
            this.zoneChangeCounter = ((Card)mageObject).getZoneChangeCounter();
        } else {
            zoneChangeCounter = 0;
        }
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public int getZoneChangeCounter() {
        return zoneChangeCounter;
    }

    @Override
    public int compareTo(MageObjectReference o) {
        if (o.getSourceId().equals(this.sourceId)) {
            return o.getZoneChangeCounter() - this.zoneChangeCounter;
        }
        return o.getSourceId().compareTo(sourceId);
    }

    @Override
    public boolean equals(Object v) {
        if (v instanceof MageObjectReference) {
            if (((MageObjectReference)v).getSourceId().equals(this.sourceId)) {
                return ((MageObjectReference)v).getZoneChangeCounter() == this.zoneChangeCounter;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.sourceId != null ? this.sourceId.hashCode() + this.zoneChangeCounter : 0);
        return hash;
    }

    public boolean refersTo(Permanent permanent) {
        return permanent.getZoneChangeCounter()== zoneChangeCounter && permanent.getId().equals(sourceId);
    }

    public boolean refersTo(Card card) {
        return card.getZoneChangeCounter()== zoneChangeCounter && card.getId().equals(sourceId);
    }

    public boolean refersTo(MageObject mageObject) {
        if (mageObject instanceof Permanent) {
            return equals(((Permanent)mageObject));
        } else if (mageObject instanceof Card) {
            return equals(((Card)mageObject));
        }
        return mageObject.getId().equals(sourceId);
    }

}
