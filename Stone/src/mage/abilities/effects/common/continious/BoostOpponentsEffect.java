package mage.abilities.effects.common.continious;

import mage.abilities.Ability;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.constants.Duration;
import mage.constants.Layer;
import mage.constants.Outcome;
import mage.constants.SubLayer;
import mage.filter.common.FilterCreaturePermanent;
import mage.game.Game;
import mage.game.permanent.Permanent;

import java.util.Set;
import java.util.UUID;

public class BoostOpponentsEffect extends ContinuousEffectImpl {
    protected int power;
    protected int toughness;
    protected FilterCreaturePermanent filter;

    public BoostOpponentsEffect(int power, int toughness, Duration duration) {
        this(power, toughness, duration, new FilterCreaturePermanent("Creatures"));
    }

    public BoostOpponentsEffect(int power, int toughness, Duration duration, FilterCreaturePermanent filter) {
        super(duration, Layer.PTChangingEffects_7, SubLayer.ModifyPT_7c, toughness < 0 ? Outcome.UnboostCreature : Outcome.BoostCreature);
        this.power = power;
        this.toughness = toughness;
        this.filter = filter;
        setText();
    }

    public BoostOpponentsEffect(final BoostOpponentsEffect effect) {
        super(effect);
        this.power = effect.power;
        this.toughness = effect.toughness;
        this.filter = effect.filter.copy();
    }

    @Override
    public BoostOpponentsEffect copy() {
        return new BoostOpponentsEffect(this);
    }

    @Override
    public void init(Ability source, Game game) {
        super.init(source, game);
        if (this.affectedObjectsSet) {
            Set<UUID> opponents = game.getOpponents(source.getControllerId());
            for (Permanent perm: game.getBattlefield().getActivePermanents(filter, source.getControllerId(), source.getSourceId(), game)) {
                if (opponents.contains(perm.getControllerId())) {
                    objects.add(perm.getId());
                }
            }
        }
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Set<UUID> opponents = game.getOpponents(source.getControllerId());
        for (Permanent perm: game.getBattlefield().getActivePermanents(filter, source.getControllerId(), source.getSourceId(), game)) {
            if (!this.affectedObjectsSet || objects.contains(perm.getId())) {
                if (opponents.contains(perm.getControllerId())) {
                    perm.addPower(power);
                    perm.addToughness(toughness);
                }
            }
        }
        return true;
    }

    private void setText() {
        StringBuilder sb = new StringBuilder();
        sb.append(filter.getMessage());
        sb.append(" your opponents control get ").append(String.format("%1$+d/%2$+d", power, toughness));
        sb.append((duration== Duration.EndOfTurn?" until end of turn":""));
        staticText = sb.toString();
    }
}
