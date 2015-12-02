/*
 *  Copyright 2011 BetaSteward_at_googlemail.com. All rights reserved.
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
package mage.abilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import mage.abilities.costs.OptionalAdditionalModeSourceCosts;
import mage.cards.Card;
import mage.constants.Outcome;
import mage.constants.TargetController;
import mage.game.Game;
import mage.players.Player;
import mage.target.common.TargetOpponent;
import mage.util.CardUtil;

/**
 *
 * @author BetaSteward_at_googlemail.com
 */
public class Modes extends LinkedHashMap<UUID, Mode> {

    private Mode mode; // the current mode of the selected modes
    private final ArrayList<Mode> selectedModes = new ArrayList<>();
    private int minModes;
    private int maxModes;
    private TargetController modeChooser;
    private boolean eachModeMoreThanOnce; // each mode can be selected multiple times during one choice
    private boolean eachModeOnlyOnce; // state if each mode can be chosen only once as long as the source object exists

    public Modes() {
        this.mode = new Mode();
        this.put(mode.getId(), mode);
        this.minModes = 1;
        this.maxModes = 1;
        this.selectedModes.add(mode);
        this.modeChooser = TargetController.YOU;
        this.eachModeOnlyOnce = false;
        this.eachModeMoreThanOnce = false;
    }

    public Modes(final Modes modes) {
        for (Map.Entry<UUID, Mode> entry : modes.entrySet()) {
            this.put(entry.getKey(), entry.getValue().copy());
        }
        this.minModes = modes.minModes;
        this.maxModes = modes.maxModes;

        if (modes.size() == 1) {
            this.mode = values().iterator().next();
            this.selectedModes.add(mode);
        } else {
            // probably there is still a problem with copying modes with the same mode selected multiple times.
            for (Mode selectedMode : modes.getSelectedModes()) {
                Mode copiedMode = selectedMode.copy();
                this.selectedModes.add(copiedMode);
                if (modes.getSelectedModes().size() == 1) {
                    this.mode = copiedMode;
                } else {
                    if (selectedMode.equals(modes.getMode())) {
                        this.mode = copiedMode;
                    }
                }
            }
        }
        this.modeChooser = modes.modeChooser;
        this.eachModeOnlyOnce = modes.eachModeOnlyOnce;
        this.eachModeMoreThanOnce = modes.eachModeMoreThanOnce;
    }

    public Modes copy() {
        return new Modes(this);
    }

    public Mode getMode() {
        return mode;
    }

    public UUID getModeId(int index) {
        int idx = 0;
        for (Mode currentMode : this.values()) {
            idx++;
            if (idx == index) {
                return currentMode.getId();
            }
        }
        return null;
    }

    public ArrayList<Mode> getSelectedModes() {
        return selectedModes;
    }

    public void setMinModes(int minModes) {
        this.minModes = minModes;
    }

    public int getMinModes() {
        return this.minModes;
    }

    public void setMaxModes(int maxModes) {
        this.maxModes = maxModes;
    }

    public int getMaxModes() {
        return this.maxModes;
    }

    public void setModeChooser(TargetController modeChooser) {
        this.modeChooser = modeChooser;
    }

    public TargetController getModeChooser() {
        return this.modeChooser;
    }

    public void setActiveMode(Mode mode) {
        if (selectedModes.contains(mode)) {
            this.mode = mode;
        }
    }

    public void addMode(Mode mode) {
        this.put(mode.getId(), mode);
    }

    public boolean choose(Game game, Ability source) {
        if (this.size() > 1) {
            this.selectedModes.clear();
            // check if mode modifying abilities exist
            Card card = game.getCard(source.getSourceId());
            if (card != null) {
                for (Ability modeModifyingAbility : card.getAbilities()) {
                    if (modeModifyingAbility instanceof OptionalAdditionalModeSourceCosts) {
                        ((OptionalAdditionalModeSourceCosts) modeModifyingAbility).addOptionalAdditionalModeCosts(source, game);
                    }
                }
            }
            // check if all modes can be activated automatically
            if (this.size() == this.getMinModes() && !isEachModeMoreThanOnce()) {
                Set<UUID> onceSelectedModes = null;
                if (isEachModeOnlyOnce()) {
                    onceSelectedModes = getAlreadySelectedModes(source, game);
                }
                for (Mode mode : this.values()) {
                    if ((!isEachModeOnlyOnce() || onceSelectedModes == null || !onceSelectedModes.contains(mode.getId()))
                            && mode.getTargets().canChoose(source.getSourceId(), source.getControllerId(), game)) {
                        this.selectedModes.add(mode.copy());
                    }
                }
                if (isEachModeOnlyOnce()) {
                    setAlreadySelectedModes(selectedModes, source, game);
                }
                return selectedModes.size() > 0;
            }

            // 700.2d
            // Some spells and abilities specify that a player other than their controller chooses a mode for it.
            // In that case, the other player does so when the spell or ability’s controller normally would do so.
            // If there is more than one other player who could make such a choice, the spell or ability’s controller decides which of those players will make the choice.
            UUID playerId = null;
            if (modeChooser == TargetController.OPPONENT) {
                TargetOpponent targetOpponent = new TargetOpponent();
                if (targetOpponent.choose(Outcome.Benefit, source.getControllerId(), source.getSourceId(), game)) {
                    playerId = targetOpponent.getFirstTarget();
                }
            } else {
                playerId = source.getControllerId();
            }
            if (playerId == null) {
                return false;
            }
            Player player = game.getPlayer(playerId);

            // player chooses modes manually
            this.mode = null;
            while (this.selectedModes.size() < this.getMaxModes()) {
                Mode choice = player.chooseMode(this, source, game);
                if (choice == null) {
                    if (isEachModeOnlyOnce()) {
                        setAlreadySelectedModes(selectedModes, source, game);
                    }
                    return this.selectedModes.size() >= this.getMinModes();
                }
                this.selectedModes.add(choice.copy());
                if (mode == null) {
                    mode = choice;
                }
            }
            if (isEachModeOnlyOnce()) {
                setAlreadySelectedModes(selectedModes, source, game);
            }
            return true;
        }
        if (mode == null) {
            this.selectedModes.clear();
            Mode copiedMode = this.values().iterator().next().copy();
            this.selectedModes.add(copiedMode);
            this.setActiveMode(copiedMode);
        }
        if (isEachModeOnlyOnce()) {
            setAlreadySelectedModes(selectedModes, source, game);
        }
        return true;
    }

    private void setAlreadySelectedModes(ArrayList<Mode> selectedModes, Ability source, Game game) {
        String key = getKey(source, game);
        Set<UUID> onceSelectedModes = (Set<UUID>) game.getState().getValue(key);
        if (onceSelectedModes == null) {
            onceSelectedModes = new HashSet<>();
        }
        for (Mode mode : selectedModes) {
            onceSelectedModes.add(mode.getId());
        }

        game.getState().setValue(key, onceSelectedModes);
    }

    private Set<UUID> getAlreadySelectedModes(Ability source, Game game) {
        return (Set<UUID>) game.getState().getValue(getKey(source, game));
    }

    private String getKey(Ability source, Game game) {
        return CardUtil.getObjectZoneString("selectedModes", source.getSourceId(), game, game.getState().getZoneChangeCounter(source.getSourceId()), false);
    }

    public List<Mode> getAvailableModes(Ability source, Game game) {
        List<Mode> availableModes = new ArrayList<>();
        Set<UUID> nonAvailableModes;
        if (isEachModeMoreThanOnce()) {
            nonAvailableModes = new HashSet<>();
        } else {
            nonAvailableModes = getAlreadySelectedModes(source, game);
        }
        for (Mode mode : this.values()) {
            if (isEachModeOnlyOnce() && nonAvailableModes != null && nonAvailableModes.contains(mode.getId())) {
                continue;
            }
            availableModes.add(mode);
        }
        return availableModes;
    }

    public String getText() {
        if (this.size() <= 1) {
            return this.getMode().getEffects().getText(this.getMode());
        }
        StringBuilder sb = new StringBuilder();
        if (this.getMinModes() == 1 && this.getMaxModes() == 3) {
            sb.append("choose one or more ");
        } else if (this.getMinModes() == 1 && this.getMaxModes() == 2) {
            sb.append("choose one or both ");
        } else if (this.getMinModes() == 2 && this.getMaxModes() == 2) {
            sb.append("choose two ");
        } else if (this.getMinModes() == 3 && this.getMaxModes() == 3) {
            sb.append("choose three ");
        } else {
            sb.append("choose one ");
        }
        if (isEachModeOnlyOnce()) {
            sb.append("that hasn't been chosen ");
        }
        if (isEachModeMoreThanOnce()) {
            sb.append(". You may choose the same mode more than once.<br>");
        } else {
            sb.append("&mdash;<br>");
        }
        for (Mode mode : this.values()) {
            sb.append("&bull  ");
            sb.append(mode.getEffects().getTextStartingUpperCase(mode));
            sb.append("<br>");
        }
        return sb.toString();
    }

    public String getText(String sourceName) {
        String text = getText();
        text = text.replace("{this}", sourceName);
        text = text.replace("{source}", sourceName);
        return text;
    }

    public boolean isEachModeOnlyOnce() {
        return eachModeOnlyOnce;
    }

    public void setEachModeOnlyOnce(boolean eachModeOnlyOnce) {
        this.eachModeOnlyOnce = eachModeOnlyOnce;
    }

    public boolean isEachModeMoreThanOnce() {
        return eachModeMoreThanOnce;
    }

    public void setEachModeMoreThanOnce(boolean eachModeMoreThanOnce) {
        this.eachModeMoreThanOnce = eachModeMoreThanOnce;
    }

}
