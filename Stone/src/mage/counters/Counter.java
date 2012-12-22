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

package mage.counters;

import java.io.Serializable;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class Counter<T extends Counter<T>> implements Serializable {

    protected String name;
    protected int count;

    public Counter(String name) {
        this.name = name;
        this.count = 1;
    }

    public Counter(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public Counter(Counter counter) {
        this.name = counter.name;
        this.count = counter.count;
    }

    public void add() {
        count++;
    }

    public void add(int amount) {
        count += amount;
    }

    public void remove() {
        if (count > 0) {
            count--;
        }
    }

    public void remove(int amount) {
        if (count > amount) {
            count -= amount;
        }
        else {
            count = 0;
        }
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public T copy() {
        return (T)new Counter(this);
    }
}
