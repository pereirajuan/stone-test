package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

import java.util.Arrays;
import java.util.List;

/**
 * @author TheElk801
 */
public final class KamigawaNeonDynasty extends ExpansionSet {

    private static final List<String> unfinished = Arrays.asList("Bronzeplate Boar", "Leech Gauntlet", "Lizard Blades", "Simian Sling", "The Reality Chip");
    private static final KamigawaNeonDynasty instance = new KamigawaNeonDynasty();

    public static KamigawaNeonDynasty getInstance() {
        return instance;
    }

    private KamigawaNeonDynasty() {
        super("Kamigawa: Neon Dynasty", "NEO", ExpansionSet.buildDate(2022, 2, 18), SetType.EXPANSION);
        this.blockName = "Kamigawa: Neon Dynasty";
        this.hasBoosters = true;
        this.hasBasicLands = true;
        this.numBoosterDoubleFaced = 1; // temporary test fix

        cards.add(new SetCardInfo("Akki War Paint", 132, Rarity.COMMON, mage.cards.a.AkkiWarPaint.class));
        cards.add(new SetCardInfo("Asari Captain", 215, Rarity.UNCOMMON, mage.cards.a.AsariCaptain.class));
        cards.add(new SetCardInfo("Atsushi, the Blazing Sky", 134, Rarity.MYTHIC, mage.cards.a.AtsushiTheBlazingSky.class));
        cards.add(new SetCardInfo("Befriending the Moths", 4, Rarity.COMMON, mage.cards.b.BefriendingTheMoths.class));
        cards.add(new SetCardInfo("Bronzeplate Boar", 135, Rarity.UNCOMMON, mage.cards.b.BronzeplateBoar.class));
        cards.add(new SetCardInfo("Covert Technician", 49, Rarity.UNCOMMON, mage.cards.c.CovertTechnician.class));
        cards.add(new SetCardInfo("Dokuchi Shadow-Walker", 94, Rarity.COMMON, mage.cards.d.DokuchiShadowWalker.class));
        cards.add(new SetCardInfo("Enthusiastic Mechanaut", 218, Rarity.UNCOMMON, mage.cards.e.EnthusiasticMechanaut.class));
        cards.add(new SetCardInfo("Era of Enlightenment", 11, Rarity.COMMON, mage.cards.e.EraOfEnlightenment.class));
        cards.add(new SetCardInfo("Forest", 301, Rarity.LAND, mage.cards.basiclands.Forest.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Goro-Goro, Disciple of Ryusei", 145, Rarity.RARE, mage.cards.g.GoroGoroDiscipleOfRyusei.class));
        cards.add(new SetCardInfo("Greater Tanuki", 189, Rarity.COMMON, mage.cards.g.GreaterTanuki.class));
        cards.add(new SetCardInfo("Hand of Enlightenment", 11, Rarity.COMMON, mage.cards.h.HandOfEnlightenment.class));
        cards.add(new SetCardInfo("Hidetsugu, Devouring Chaos", 99, Rarity.RARE, mage.cards.h.HidetsuguDevouringChaos.class));
        cards.add(new SetCardInfo("Imperial Moth", 4, Rarity.COMMON, mage.cards.i.ImperialMoth.class));
        cards.add(new SetCardInfo("Imperial Subduer", 19, Rarity.COMMON, mage.cards.i.ImperialSubduer.class));
        cards.add(new SetCardInfo("Island", 295, Rarity.LAND, mage.cards.basiclands.Island.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Jukai Naturalist", 225, Rarity.UNCOMMON, mage.cards.j.JukaiNaturalist.class));
        cards.add(new SetCardInfo("Junji, the Midnight Sky", 102, Rarity.MYTHIC, mage.cards.j.JunjiTheMidnightSky.class));
        cards.add(new SetCardInfo("Kaito Shizuki", 226, Rarity.MYTHIC, mage.cards.k.KaitoShizuki.class));
        cards.add(new SetCardInfo("Leech Gauntlet", 106, Rarity.UNCOMMON, mage.cards.l.LeechGauntlet.class));
        cards.add(new SetCardInfo("Lizard Blades", 153, Rarity.RARE, mage.cards.l.LizardBlades.class));
        cards.add(new SetCardInfo("Mountain", 299, Rarity.LAND, mage.cards.basiclands.Mountain.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Nameless Conqueror", 162, Rarity.COMMON, mage.cards.n.NamelessConqueror.class));
        cards.add(new SetCardInfo("Plains", 293, Rarity.LAND, mage.cards.basiclands.Plains.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Satoru Umezawa", 234, Rarity.RARE, mage.cards.s.SatoruUmezawa.class));
        cards.add(new SetCardInfo("Spirited Companion", 38, Rarity.COMMON, mage.cards.s.SpiritedCompanion.class));
        cards.add(new SetCardInfo("Surgehacker Mech", 260, Rarity.RARE, mage.cards.s.SurgehackerMech.class));
        cards.add(new SetCardInfo("Swamp", 297, Rarity.LAND, mage.cards.basiclands.Swamp.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("The Modern Age", 66, Rarity.COMMON, mage.cards.t.TheModernAge.class));
        cards.add(new SetCardInfo("The Shattered States Era", 162, Rarity.COMMON, mage.cards.t.TheShatteredStatesEra.class));
        cards.add(new SetCardInfo("Twinshot Sniper", 168, Rarity.UNCOMMON, mage.cards.t.TwinshotSniper.class));
        cards.add(new SetCardInfo("Unstoppable Ogre", 169, Rarity.COMMON, mage.cards.u.UnstoppableOgre.class));
        cards.add(new SetCardInfo("Vector Glider", 66, Rarity.COMMON, mage.cards.v.VectorGlider.class));

        cards.removeIf(setCardInfo -> unfinished.contains(setCardInfo.getName())); // remove when mechanic is fully implemented
    }
}
