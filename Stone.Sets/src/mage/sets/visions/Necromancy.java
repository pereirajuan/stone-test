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
package mage.sets.visions;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbilityImpl;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.common.LeavesBattlefieldTriggeredAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.common.delayed.AtTheBeginOfNextCleanupDelayedTriggeredAbility;
import mage.abilities.condition.common.SourceOnBattelfieldCondition;
import mage.abilities.decorator.ConditionalTriggeredAbility;
import mage.abilities.effects.AsThoughEffectImpl;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.CreateDelayedTriggeredAbilityEffect;
import mage.abilities.effects.common.SacrificeSourceEffect;
import mage.abilities.effects.common.continious.SourceEffect;
import mage.abilities.keyword.EnchantAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.constants.AsThoughEffectType;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.Rarity;
import mage.constants.SubLayer;
import mage.constants.Zone;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.permanent.PermanentIdPredicate;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.Target;
import mage.target.common.TargetCreaturePermanent;

/**
 *
 * @author LevelX2
 */
public class Necromancy extends CardImpl {

    public Necromancy(UUID ownerId) {
        super(ownerId, 14, "Necromancy", Rarity.UNCOMMON, new CardType[]{CardType.ENCHANTMENT}, "{2}{B}");
        this.expansionSetCode = "VIS";

        this.color.setBlack(true);

        // You may cast Necromancy as though it had flash. If you cast it any time a sorcery couldn't have been cast, the controller of the permanent it becomes sacrifices it at the beginning of the next cleanup step.
        this.addAbility(new SimpleStaticAbility(Zone.ALL, new CastSourceAsThoughItHadFlashEffect(this, Duration.EndOfGame, true)));        
        
        // When Necromancy enters the battlefield, if it's on the battlefield, it becomes an Aura with "enchant creature put onto the battlefield with Necromancy." 
        // Put target creature card from a graveyard onto the battlefield under your control and attach Necromancy to it. 
        // When Necromancy leaves the battlefield, that creature's controller sacrifices it.
        Ability ability = new ConditionalTriggeredAbility(
                new EntersBattlefieldTriggeredAbility(new NecromancyReAttachEffect(), false),
                SourceOnBattelfieldCondition.getInstance(),
                "When {this} enters the battlefield, if it's on the battlefield,  it becomes an Aura with \"enchant creature put onto the battlefield with {this}.\" Put target creature card from a graveyard onto the battlefield under your control and attach {this} to it.");        
        this.addAbility(ability);
        this.addAbility(new LeavesBattlefieldTriggeredAbility(new NecromancyLeavesBattlefieldTriggeredEffect(), false));     
    }

    public Necromancy(final Necromancy card) {
        super(card);
    }

    @Override
    public Necromancy copy() {
        return new Necromancy(this);
    }
}


class CastSourceAsThoughItHadFlashEffect extends AsThoughEffectImpl {

    private final boolean sacrificeIfCastAsInstant;

    public CastSourceAsThoughItHadFlashEffect(Card card, Duration duration, boolean sacrificeIfCastAsInstant) {
        super(AsThoughEffectType.CAST_AS_INSTANT, duration, Outcome.Benefit);
        this.sacrificeIfCastAsInstant = sacrificeIfCastAsInstant;
        if (sacrificeIfCastAsInstant) {
            card.addAbility(new CastAtInstantTimeTriggeredAbility());
        }
        setText();
    }


    public CastSourceAsThoughItHadFlashEffect(final CastSourceAsThoughItHadFlashEffect effect) {
        super(effect);
        this.sacrificeIfCastAsInstant = effect.sacrificeIfCastAsInstant;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public CastSourceAsThoughItHadFlashEffect copy() {
        return new CastSourceAsThoughItHadFlashEffect(this);
    }

    @Override
    public boolean applies(UUID affectedSpellId, Ability source, UUID affectedControllerId, Game game) {
        return source.getSourceId().equals(affectedSpellId);
    }

    private String setText() {
        StringBuilder sb = new StringBuilder("You may cast  {this} as though it had flash");
        if (sacrificeIfCastAsInstant) {
            sb.append(". If you cast it any time a sorcery couldn't have been cast, the controller of the permanent it becomes sacrifices it at the beginning of the next cleanup step");
        } 
        return sb.toString();
    }
}

class CastAtInstantTimeTriggeredAbility extends TriggeredAbilityImpl {
    public CastAtInstantTimeTriggeredAbility() {
        super(Zone.BATTLEFIELD, new CreateDelayedTriggeredAbilityEffect(new AtTheBeginOfNextCleanupDelayedTriggeredAbility(new SacrificeSourceEffect())));
    }

    public CastAtInstantTimeTriggeredAbility(final CastAtInstantTimeTriggeredAbility ability) {
        super(ability);
    }

    @Override
    public CastAtInstantTimeTriggeredAbility copy() {
        return new CastAtInstantTimeTriggeredAbility(this);
    }

    @Override
    public boolean checkTrigger(GameEvent event, Game game) {
        // The sacrifice occurs only if you cast it using its own ability. If you cast it using some other
        // effect (for instance, if it gained flash from Vedalken Orrery), then it won't be sacrificed.
        // CHECK
        if (event.getType() == GameEvent.EventType.SPELL_CAST && event.getSourceId().equals(getSourceId())) {
           return !game.canPlaySorcery(event.getPlayerId());
        }
        return false;
    }

    @Override
    public String getRule() {
        return "If you cast it any time a sorcery couldn't have been cast, the controller of the permanent it becomes sacrifices it at the beginning of the next cleanup step.";
    }
}

class NecromancyReAttachEffect extends OneShotEffect {
    
    public NecromancyReAttachEffect() {
        super(Outcome.Benefit);
        this.staticText = "it becomes an Aura with \"enchant creature put onto the battlefield with {this}\"";
    }
    
    public NecromancyReAttachEffect(final NecromancyReAttachEffect effect) {
        super(effect);
    }
    
    @Override
    public NecromancyReAttachEffect copy() {
        return new NecromancyReAttachEffect(this);
    }
    
    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Permanent enchantment = game.getPermanent(source.getSourceId());
        
        if (controller != null && enchantment != null) {
            Card cardInGraveyard = game.getCard(enchantment.getAttachedTo());
            if (cardInGraveyard == null) {
                return true;
            }
            game.addEffect(new NecromancyChangeAbilityEffect(), source);
            // put card into play
            controller.putOntoBattlefieldWithInfo(cardInGraveyard, game, Zone.GRAVEYARD, source.getSourceId());
            Permanent enchantedCreature = game.getPermanent(cardInGraveyard.getId());
            
            FilterCreaturePermanent filter = new FilterCreaturePermanent("enchant creature put onto the battlefield with Necromancy");
            filter.add(new PermanentIdPredicate(cardInGraveyard.getId()));
            Target target = new TargetCreaturePermanent(filter);
            //enchantAbility.setTargetName(target.getTargetName());
            if (enchantedCreature != null) {
                target.addTarget(enchantedCreature.getId(), source, game);
                enchantment.getSpellAbility().getTargets().clear();
                enchantment.getSpellAbility().getTargets().add(target);
                enchantedCreature.addAttachment(enchantment.getId(), game);
            }
            return true;
        }
        
        return false;
    }
}
        
class NecromancyLeavesBattlefieldTriggeredEffect extends OneShotEffect {
    
    public NecromancyLeavesBattlefieldTriggeredEffect() {
        super(Outcome.Benefit);
        this.staticText = "enchanted creature's controller sacrifices it";
    }
    
    public NecromancyLeavesBattlefieldTriggeredEffect(final NecromancyLeavesBattlefieldTriggeredEffect effect) {
        super(effect);
    }
    
    @Override
    public NecromancyLeavesBattlefieldTriggeredEffect copy() {
        return new NecromancyLeavesBattlefieldTriggeredEffect(this);
    }
    
    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Permanent sourcePermanent = game.getPermanentOrLKIBattlefield(source.getSourceId());
        if (controller != null && sourcePermanent != null) {
            if (sourcePermanent.getAttachedTo() != null) {
                Permanent attachedTo = game.getPermanent(sourcePermanent.getAttachedTo());
                if (attachedTo != null) {
                    attachedTo.sacrifice(source.getSourceId(), game);
                }
            }
            return true;
        }
        return false;
    }
}

class NecromancyAttachEffect extends OneShotEffect {

    public NecromancyAttachEffect(Outcome outcome) {
        super(outcome);
    }

    public NecromancyAttachEffect(Outcome outcome, String rule) {
        super(outcome);
        staticText = rule;
    }

    public NecromancyAttachEffect(final NecromancyAttachEffect effect) {
        super(effect);
    }

    @Override
    public NecromancyAttachEffect copy() {
        return new NecromancyAttachEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card card = game.getCard(source.getFirstTarget());
        if (card != null && game.getState().getZone(source.getFirstTarget()).equals(Zone.GRAVEYARD)) {
            // Card have no attachedTo attribute yet so write ref only to enchantment now
            Permanent enchantment = game.getPermanent(source.getSourceId());
            if (enchantment != null) {
                enchantment.attachTo(card.getId(), game);
            }
            return true;
        }
        return false;
    }

}

class NecromancyChangeAbilityEffect extends ContinuousEffectImpl implements SourceEffect {

    private final static Ability newAbility = new EnchantAbility("creature put onto the battlefield with Necromancy");
    
    static {
        newAbility.setRuleAtTheTop(true);
    }
    
    public NecromancyChangeAbilityEffect() {
        super(Duration.Custom, Outcome.AddAbility);
        staticText = "it becomes an Aura with \"enchant creature put onto the battlefield with {this}\"";
    }


    public NecromancyChangeAbilityEffect(final NecromancyChangeAbilityEffect effect) {
        super(effect);
    }

    @Override
    public NecromancyChangeAbilityEffect copy() {
        return new NecromancyChangeAbilityEffect(this);
    }
    
    @Override
    public void init(Ability source, Game game) {
        super.init(source, game);
        getAffectedObjects().add(source.getSourceId());
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = game.getPermanent(source.getSourceId());
        if (permanent != null) {
            switch (layer) {
                case TypeChangingEffects_4:
                    if (sublayer == SubLayer.NA) {
                        if (!permanent.getSubtype().contains("Aura")) {
                            permanent.getSubtype().add("Aura");
                        }
                    }
                    break;
                case AbilityAddingRemovingEffects_6:
                    if (sublayer == SubLayer.NA) {
                        permanent.addAbility(newAbility, source.getSourceId(), game);                    
                    }
            }
            return true;            
        }   
        this.discard();
        return false;
    }

    @Override
    public boolean hasLayer(Layer layer) {
        return Layer.AbilityAddingRemovingEffects_6.equals(layer) || Layer.TypeChangingEffects_4.equals(layer);
    }
        
}
