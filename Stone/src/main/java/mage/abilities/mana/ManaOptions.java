package mage.abilities.mana;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.costs.Cost;
import mage.abilities.costs.common.TapSourceCost;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.events.ManaEvent;
import mage.players.Player;
import org.apache.log4j.Logger;

/**
 *
 * @author BetaSteward_at_googlemail.com
 *
 * this class is used to build a list of all possible mana combinations it can
 * be used to find all the ways to pay a mana cost or all the different mana
 * combinations available to a player
 *
 * TODO: Conditional Mana is not supported yet. The mana adding removes the
 * condition of conditional mana
 *
 */
public class ManaOptions extends ArrayList<Mana> {

    private static final Logger logger = Logger.getLogger(ManaOptions.class);

    public ManaOptions() {
    }

    public ManaOptions(final ManaOptions options) {
        for (Mana mana : options) {
            this.add(mana.copy());
        }
    }

    public void addMana(List<ActivatedManaAbilityImpl> abilities, Game game) {
        if (isEmpty()) {
            this.add(new Mana());
        }
        if (!abilities.isEmpty()) {
            if (abilities.size() == 1) {
                //if there is only one mana option available add it to all the existing options
                List<Mana> netManas = abilities.get(0).getNetMana(game);
                if (netManas.size() == 1) {
                    checkManaReplacementAndTriggeredMana(abilities.get(0), game, netManas.get(0));
                    addMana(netManas.get(0));
                    addTriggeredMana(game, abilities.get(0));
                } else if (netManas.size() > 1) {
                    addManaVariation(netManas, abilities.get(0), game);
                }

            } else { // mana source has more than 1 ability
                //perform a union of all existing options and the new options
                List<Mana> copy = copy();
                this.clear();
                for (ActivatedManaAbilityImpl ability : abilities) {
                    for (Mana netMana : ability.getNetMana(game)) {
                        checkManaReplacementAndTriggeredMana(ability, game, netMana);
                        for (Mana triggeredManaVariation : getTriggeredManaVariations(game, ability, netMana)) {
                            SkipAddMana:
                            for (Mana mana : copy) {
                                Mana newMana = new Mana();
                                newMana.add(mana);
                                newMana.add(triggeredManaVariation);
                                for (Mana existingMana : this) {
                                    if (existingMana.equalManaValue(newMana)) {
                                        continue SkipAddMana;
                                    }
                                    Mana moreValuable = Mana.getMoreValuableMana(newMana, existingMana);
                                    if (moreValuable != null) {
                                        // only keep the more valuable mana
                                        existingMana.setToMana(moreValuable);
                                        continue SkipAddMana;
                                    }
                                }
                                this.add(newMana);
                            }
                        }

                    }
                }
            }
        }
    }

    private void addManaVariation(List<Mana> netManas, ActivatedManaAbilityImpl ability, Game game) {
        List<Mana> copy = copy();
        this.clear();
        for (Mana netMana : netManas) {
            for (Mana mana : copy) {
                if (!hasTapCost(ability) || checkManaReplacementAndTriggeredMana(ability, game, netMana)) {
                    Mana newMana = new Mana();
                    newMana.add(mana);
                    newMana.add(netMana);
                    this.add(newMana);
                }
            }
        }
    }

    private List<List<Mana>> getSimulatedTriggeredManaFromPlayer(Game game, Ability ability) {
        Player player = game.getPlayer(ability.getControllerId());
        List<List<Mana>> newList = new ArrayList<>();
        if (player != null) {
            newList.addAll(player.getAvailableTriggeredMana());
            player.getAvailableTriggeredMana().clear();
        }
        return newList;
    }

    /**
     * Generates triggered mana and checks replacement of Tapped_For_Mana event.
     * Also generates triggered mana for MANA_ADDED event.
     *
     * @param ability
     * @param game
     * @param mana
     * @return false if mana production was completely replaced
     */
    private boolean checkManaReplacementAndTriggeredMana(Ability ability, Game game, Mana mana) {
        if (hasTapCost(ability)) {
            ManaEvent event = new ManaEvent(GameEvent.EventType.TAPPED_FOR_MANA, ability.getSourceId(), ability.getSourceId(), ability.getControllerId(), mana);
            if (game.replaceEvent(event)) {
                return false;
            }
            game.fireEvent(event);
        }
        ManaEvent manaEvent = new ManaEvent(GameEvent.EventType.MANA_ADDED, ability.getSourceId(), ability.getSourceId(), ability.getControllerId(), mana);
        manaEvent.setData(mana.toString());
        game.fireEvent(manaEvent);
        return true;
    }

    public boolean hasTapCost(Ability ability) {
        for (Cost cost : ability.getCosts()) {
            if (cost instanceof TapSourceCost) {
                return true;
            }
        }
        return false;
    }

    public void addManaWithCost(List<ActivatedManaAbilityImpl> abilities, Game game) {
        int replaces = 0;
        if (isEmpty()) {
            this.add(new Mana()); // needed if this is the first available mana, otherwise looping over existing options woold not loop
        }
        if (!abilities.isEmpty()) {
            if (abilities.size() == 1) {
                List<Mana> netManas = abilities.get(0).getNetMana(game);
                if (netManas.size() > 0) { // ability can produce mana
                    ActivatedManaAbilityImpl ability = abilities.get(0);
                    // The ability has no mana costs
                    if (ability.getManaCosts().isEmpty()) { // No mana costs, so no mana to subtract from available
                        if (netManas.size() == 1) {
                            checkManaReplacementAndTriggeredMana(ability, game, netManas.get(0));
                            addMana(netManas.get(0));
                            addTriggeredMana(game, ability);
                        } else {
                            List<Mana> copy = copy();
                            this.clear();
                            for (Mana netMana : netManas) {
                                checkManaReplacementAndTriggeredMana(ability, game, netMana);
                                for (Mana triggeredManaVariation : getTriggeredManaVariations(game, ability, netMana)) {
                                    for (Mana mana : copy) {
                                        Mana newMana = new Mana();
                                        newMana.add(mana);
                                        newMana.add(triggeredManaVariation);
                                        this.add(newMana);
                                    }
                                }
                            }
                        }
                    } else {// The ability has mana costs
                        List<Mana> copy = copy();
                        this.clear();
                        for (Mana netMana : netManas) {
                            checkManaReplacementAndTriggeredMana(ability, game, netMana);
                            for (Mana triggeredManaVariation : getTriggeredManaVariations(game, ability, netMana)) {
                                for (Mana prevMana : copy) {
                                    Mana startingMana = prevMana.copy();
                                    if (!subtractCostAddMana(ability.getManaCosts().getMana(), triggeredManaVariation, ability.getCosts().isEmpty(), startingMana)) {
                                        // the starting mana includes mana parts that the increased mana does not include, so add starting mana also as an option
                                        add(prevMana);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                //perform a union of all existing options and the new options
                List<Mana> copy = copy();
                this.clear();
                for (ActivatedManaAbilityImpl ability : abilities) {
                    List<Mana> netManas = ability.getNetMana(game);
                    if (ability.getManaCosts().isEmpty()) {
                        for (Mana netMana : netManas) {
                            checkManaReplacementAndTriggeredMana(ability, game, netMana);
                            for (Mana triggeredManaVariation : getTriggeredManaVariations(game, ability, netMana)) {
                                for (Mana mana : copy) {
                                    Mana newMana = new Mana();
                                    newMana.add(mana);
                                    newMana.add(triggeredManaVariation);
                                    this.add(newMana);
                                }
                            }
                        }
                    } else {
                        for (Mana netMana : netManas) {
                            checkManaReplacementAndTriggeredMana(ability, game, netMana);
                            for (Mana triggeredManaVariation : getTriggeredManaVariations(game, ability, netMana)) {
                                for (Mana previousMana : copy) {
                                    CombineWithExisting:
                                    for (Mana manaOption : ability.getManaCosts().getManaOptions()) {
                                        Mana newMana = new Mana(previousMana);
                                        if (previousMana.includesMana(manaOption)) { // costs can be paid
                                            newMana.subtractCost(manaOption);
                                            newMana.add(triggeredManaVariation);
                                            // if the new mana is in all colors more than another already existing than replace
                                            for (Mana existingMana : this) {
                                                Mana moreValuable = Mana.getMoreValuableMana(newMana, existingMana);
                                                if (moreValuable != null) {
                                                    existingMana.setToMana(moreValuable);
                                                    replaces++;
                                                    continue CombineWithExisting;
                                                }
                                            }
                                            // no existing Mana includes this new mana so add
                                            this.add(newMana);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
        if (this.size() > 30 || replaces > 30) {
            logger.trace("ManaOptionsCosts " + this.size() + " Ign:" + replaces + " => " + this.toString());
            logger.trace("Abilities: " + abilities.toString());
        }
    }

    private List<Mana> getTriggeredManaVariations(Game game, Ability ability, Mana baseMana) {
        List<Mana> baseManaPlusTriggeredMana = new ArrayList<>();
        baseManaPlusTriggeredMana.add(baseMana);
        List<List<Mana>> availableTriggeredManaList = getSimulatedTriggeredManaFromPlayer(game, ability);
        for (List<Mana> availableTriggeredMana : availableTriggeredManaList) {
            if (availableTriggeredMana.size() == 1) {
                for (Mana prevMana : baseManaPlusTriggeredMana) {
                    prevMana.add(availableTriggeredMana.get(0));
                }
            } else if (availableTriggeredMana.size() > 1) {
                List<Mana> copy = new ArrayList<>(baseManaPlusTriggeredMana);
                baseManaPlusTriggeredMana.clear();
                for (Mana triggeredMana : availableTriggeredMana) {
                    for (Mana prevMana : copy) {
                        Mana newMana = new Mana();
                        newMana.add(prevMana);
                        newMana.add(triggeredMana);
                        baseManaPlusTriggeredMana.add(newMana);
                    }
                }
            }
        }
        return baseManaPlusTriggeredMana;
    }

    private void addTriggeredMana(Game game, Ability ability) {
        List<List<Mana>> netManaList = getSimulatedTriggeredManaFromPlayer(game, ability);
        for (List<Mana> triggeredNetMana : netManaList) {
            if (triggeredNetMana.size() == 1) {
                addMana(triggeredNetMana.get(0));
            } else if (triggeredNetMana.size() > 1) {
                // Add variations
                List<Mana> copy = copy();
                this.clear();
                for (Mana triggeredMana : triggeredNetMana) {
                    for (Mana mana : copy) {
                        Mana newMana = new Mana();
                        newMana.add(mana);
                        newMana.add(triggeredMana);
                        this.add(newMana);
                    }
                }
            }
        }
    }

    public void addMana(Mana addMana) {
        if (isEmpty()) {
            this.add(new Mana());
        }
        for (Mana mana : this) {
            mana.add(addMana);
        }
    }

    public void addMana(ManaOptions options) {
        if (isEmpty()) {
            this.add(new Mana());
        }
        if (!options.isEmpty()) {
            if (options.size() == 1) {
                //if there is only one mana option available add it to all the existing options
                addMana(options.get(0));
            } else {
                //perform a union of all existing options and the new options
                List<Mana> copy = copy();
                this.clear();
                for (Mana addMana : options) {
                    for (Mana mana : copy) {
                        Mana newMana = new Mana();
                        newMana.add(mana);
                        newMana.add(addMana);
                        this.add(newMana);
                    }
                }
            }
        }
    }

    public ManaOptions copy() {
        return new ManaOptions(this);
    }

    /**
     * Performs the simulation of a mana ability with costs
     *
     * @param cost               cost to use the ability
     * @param manaToAdd          one mana variation that can be added by using
     *                           this ability
     * @param onlyManaCosts      flag to know if the costs are mana costs only
     * @param currentMana        the mana available before the usage of the
     *                           ability
     * @param oldManaWasReplaced returns the info if the new complete mana does
     *                           replace the current mana completely
     */
    private boolean subtractCostAddMana(Mana cost, Mana manaToAdd, boolean onlyManaCosts, Mana currentMana) {
        boolean oldManaWasReplaced = false; // true if the newly created mana includes all mana possibilities of the old
        boolean repeatable = false;
        if ((manaToAdd.countColored() > 0 || manaToAdd.getAny() > 0) && manaToAdd.count() > 0 && onlyManaCosts) {
            // deactivated because it does cause loops TODO: Find reason
            repeatable = true; // only replace to any with mana costs only will be repeated if able
        }
        Mana prevMana = currentMana.copy();
        if (currentMana.includesMana(cost)) { // cost can be paid
            // generic mana costs can be paid with different colored mana, can lead to different color combinations
            if (cost.getGeneric() > 0 && cost.getGeneric() > (currentMana.getGeneric() + currentMana.getColorless())) {
                for (Mana payCombination : getPossiblePayCombinations(cost.getGeneric(), currentMana)) {
                    Mana currentManaCopy = currentMana.copy();
                    while (currentManaCopy.includesMana(payCombination)) { // loop for multiple usage if possible
                        boolean newCombinations = false;

                        Mana newMana = currentManaCopy.copy();
                        newMana.subtract(payCombination);
                        newMana.add(manaToAdd);
                        // Mana moreValuable = Mana.getMoreValuableMana(currentMana, newMana);
                        if (!isExistingManaCombination(newMana)) {
                            this.add(newMana); // add the new combination
                            newCombinations = true; // repeat the while as long there are new combinations and usage is repeatable
                            currentManaCopy = newMana.copy();
                            Mana moreValuable = Mana.getMoreValuableMana(currentMana, newMana);
                            if (!oldManaWasReplaced && newMana.equals(moreValuable)) {
                                oldManaWasReplaced = true; // the new mana includes all possibilities of the old one, so no need to add it after return
                            }
                        }
                        if (!newCombinations || !repeatable) {
                            break;
                        }
                    }

                }
            } else {
                while (currentMana.includesMana(cost)) { // loop for multiple usage if possible
                    currentMana.subtractCost(cost);
                    currentMana.add(manaToAdd);
                    if (!repeatable) {
                        break; // Stop adding multiple usages of the ability
                    }
                }
                // Don't use mana that only reduce the available mana
                if (prevMana.contains(currentMana) && prevMana.count() > currentMana.count()) {
                    currentMana.setToMana(prevMana);
                }
                Mana moreValuable = Mana.getMoreValuableMana(prevMana, currentMana);
                if (!prevMana.equals(moreValuable)) {
                    this.add(currentMana);
                    if (moreValuable != null) {
                        oldManaWasReplaced = true; // the new mana includes all possibilities of the old one
                    }
                }

            }
        }
        return oldManaWasReplaced;
    }

    private List<Mana> getPossiblePayCombinations(int number, Mana manaAvailable) {
        List<Mana> payCombinations = new ArrayList<>();
        List<String> payCombinationsStrings = new ArrayList<>();
        if (manaAvailable.countColored() > 0) {

            for (int i = 0; i < number; i++) {
                List<Mana> existingManas = new ArrayList<>();
                if (i > 0) {
                    existingManas.addAll(payCombinations);
                    payCombinations.clear();
                    payCombinationsStrings.clear();
                } else {
                    existingManas.add(new Mana());
                }
                for (Mana existingMana : existingManas) {
                    Mana manaToPayFrom = manaAvailable.copy();
                    manaToPayFrom.subtract(existingMana);
                    if (manaToPayFrom.getBlack() > 0 && !payCombinationsStrings.contains(existingMana.toString() + Mana.BlackMana(1).toString())) {
                        manaToPayFrom.subtract(Mana.BlackMana(1));
                        addManaCombination(Mana.BlackMana(1), existingMana, payCombinations, payCombinationsStrings);
                    }
                    if (manaToPayFrom.getBlue() > 0 && !payCombinationsStrings.contains(existingMana.toString() + Mana.BlueMana(1).toString())) {
                        manaToPayFrom.subtract(Mana.BlueMana(1));
                        addManaCombination(Mana.BlueMana(1), existingMana, payCombinations, payCombinationsStrings);
                    }
                    if (manaToPayFrom.getGreen() > 0 && !payCombinationsStrings.contains(existingMana.toString() + Mana.GreenMana(1).toString())) {
                        manaToPayFrom.subtract(Mana.GreenMana(1));
                        addManaCombination(Mana.GreenMana(1), existingMana, payCombinations, payCombinationsStrings);
                    }
                    if (manaToPayFrom.getRed() > 0 && !payCombinationsStrings.contains(existingMana.toString() + Mana.RedMana(1).toString())) {
                        manaToPayFrom.subtract(Mana.RedMana(1));
                        addManaCombination(Mana.RedMana(1), existingMana, payCombinations, payCombinationsStrings);
                    }
                    if (manaToPayFrom.getWhite() > 0 && !payCombinationsStrings.contains(existingMana.toString() + Mana.WhiteMana(1).toString())) {
                        manaToPayFrom.subtract(Mana.WhiteMana(1));
                        addManaCombination(Mana.WhiteMana(1), existingMana, payCombinations, payCombinationsStrings);
                    }
                    // Pay with any only needed if colored payment was not possible
                    if (payCombinations.isEmpty() && manaToPayFrom.getAny() > 0 && !payCombinationsStrings.contains(existingMana.toString() + Mana.AnyMana(1).toString())) {
                        manaToPayFrom.subtract(Mana.AnyMana(1));
                        addManaCombination(Mana.AnyMana(1), existingMana, payCombinations, payCombinationsStrings);
                    }
                }
            }
        } else {
            payCombinations.add(Mana.ColorlessMana(number));
        }
        return payCombinations;
    }

    private boolean isExistingManaCombination(Mana newMana) {
        for (Mana mana : this) {
            Mana moreValuable = Mana.getMoreValuableMana(mana, newMana);
            if (mana.equals(moreValuable)) {
                return true;
            }
        }
        return false;
    }

    private void addManaCombination(Mana mana, Mana existingMana, List<Mana> payCombinations, List<String> payCombinationsStrings) {
        Mana newMana = existingMana.copy();
        newMana.add(mana);
        payCombinations.add(newMana);
        payCombinationsStrings.add(newMana.toString());
    }

    public void removeDuplicated() {
        Set<String> list = new HashSet<>();

        for (int i = this.size() - 1; i >= 0; i--) {
            String s = this.get(i).toString();
            if (list.contains(s)) {
                // remove duplicated
                this.remove(i);
            } else {
                list.add(s);
            }
        }
        // Remove fully included variations
        for (int i = this.size() - 1; i >= 0; i--) {
            for (int ii = 0; ii < i; ii++) {
                Mana moreValuable = Mana.getMoreValuableMana(this.get(i), this.get(ii));
                if (moreValuable != null) {
                    this.get(ii).setToMana(moreValuable);
                    this.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * Checks if the given mana (cost) is already included in one available mana
     * option
     *
     * @param mana
     * @return
     */
    public boolean enough(Mana mana) {
        for (Mana avail : this) {
            if (mana.enough(avail)) {
                return true;
            }
        }
        return false;
    }
}
