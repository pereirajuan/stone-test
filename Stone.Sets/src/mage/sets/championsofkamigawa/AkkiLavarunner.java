package mage.sets.championsofkamigawa;

import mage.MageInt;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.ReplacementEffectImpl;
import mage.abilities.effects.common.FlipSourceEffect;
import mage.abilities.keyword.HasteAbility;
import mage.abilities.keyword.ProtectionAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.*;
import mage.filter.FilterCard;
import mage.filter.predicate.mageobject.ColorPredicate;
import mage.game.Game;
import mage.game.events.DamagedPlayerEvent;
import mage.game.events.GameEvent;
import mage.game.permanent.token.Token;

import java.util.UUID;

/**
 * @author Loki
 */
public class AkkiLavarunner extends CardImpl {

    public AkkiLavarunner(UUID ownerId) {
        super(ownerId, 153, "Akki Lavarunner", Rarity.RARE, new CardType[]{CardType.CREATURE}, "{3}{R}");
        this.expansionSetCode = "CHK";
        this.subtype.add("Goblin");
        this.subtype.add("Warrior");

        this.power = new MageInt(1);
        this.toughness = new MageInt(1);
        this.flipCard = true;
        this.flipCardName = "Tok-Tok, Volcano Born";

        // Haste
        this.addAbility(HasteAbility.getInstance());
        // Whenever Akki Lavarunner deals damage to an opponent, flip it.
        this.addAbility(new AkkiLavarunnerAbility());
    }

    public AkkiLavarunner(final AkkiLavarunner card) {
        super(card);
    }

    @Override
    public AkkiLavarunner copy() {
        return new AkkiLavarunner(this);
    }
}

class AkkiLavarunnerAbility extends TriggeredAbilityImpl {

    public AkkiLavarunnerAbility() {
        super(Zone.BATTLEFIELD, new FlipSourceEffect(new TokTokVolcanoBorn()));
    }

    public AkkiLavarunnerAbility(final AkkiLavarunnerAbility ability) {
        super(ability);
    }

    @Override
    public AkkiLavarunnerAbility copy() {
        return new AkkiLavarunnerAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        if (event instanceof DamagedPlayerEvent) {
            DamagedPlayerEvent damageEvent = (DamagedPlayerEvent) event;
            if (damageEvent.isCombatDamage() && this.sourceId.equals(event.getSourceId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getRule() {
        return "Whenever {this} deals damage to an opponent, flip it.";
    }
}

class TokTokVolcanoBorn extends Token {

    private static final FilterCard filter = new FilterCard("red");

    static {
        filter.add(new ColorPredicate(ObjectColor.RED));
    }

    TokTokVolcanoBorn() {
        super("Tok-Tok, Volcano Born", "");
        supertype.add("Legendary");
        cardType.add(CardType.CREATURE);
        color.setRed(true);
        subtype.add("Goblin");
        subtype.add("Shaman");
        power = new MageInt(2);
        toughness = new MageInt(2);
        this.addAbility(new ProtectionAbility(filter));
        this.addAbility(new SimpleStaticAbility(Zone.BATTLEFIELD, new TokTokVolcanoBornEffect()));
    }
}

class TokTokVolcanoBornEffect extends ReplacementEffectImpl {

    TokTokVolcanoBornEffect() {
        super(Duration.WhileOnBattlefield, Outcome.Benefit);
        staticText = "If a red source would deal damage to a player, it deals that much damage plus 1 to that player instead";
    }

    TokTokVolcanoBornEffect(final TokTokVolcanoBornEffect effect) {
        super(effect);
    }

    @Override
    public boolean checksEventType(GameEvent event, Game game) {
        return event.getType() == GameEvent.EventType.DAMAGE_PLAYER;
    }
    
    @Override
    public boolean applies(GameEvent event, Ability source, Game game) {
        Card card = game.getCard(event.getSourceId());
        if (card != null && card.getColor(game).isRed()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean replaceEvent(GameEvent event, Ability source, Game game) {
        event.setAmount(event.getAmount() + 1);
        return false;
    }

    @Override
    public TokTokVolcanoBornEffect copy() {
        return new TokTokVolcanoBornEffect(this);
    }

}
