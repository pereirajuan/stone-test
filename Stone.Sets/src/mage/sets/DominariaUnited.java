package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 * @author TheElk801
 */
public final class DominariaUnited extends ExpansionSet {

    private static final DominariaUnited instance = new DominariaUnited();

    public static DominariaUnited getInstance() {
        return instance;
    }

    private DominariaUnited() {
        super("Dominaria United", "DMU", ExpansionSet.buildDate(2022, 11, 9), SetType.EXPANSION);
        this.blockName = "Dominaria United";
        this.hasBoosters = false; // temporary
        this.hasBasicLands = true;
        this.numBoosterLands = 1;
        this.numBoosterCommon = 10;
        this.numBoosterUncommon = 3;
        this.numBoosterRare = 1;
        this.ratioBoosterMythic = 7;
        this.maxCardNumberInBooster = 281;

        cards.add(new SetCardInfo("Adarkar Wastes", 243, Rarity.RARE, mage.cards.a.AdarkarWastes.class));
        cards.add(new SetCardInfo("Archangel of Wrath", 3, Rarity.RARE, mage.cards.a.ArchangelOfWrath.class));
        cards.add(new SetCardInfo("Benalish Sleeper", 8, Rarity.COMMON, mage.cards.b.BenalishSleeper.class));
        cards.add(new SetCardInfo("Caves of Koilos", 244, Rarity.RARE, mage.cards.c.CavesOfKoilos.class));
        cards.add(new SetCardInfo("Charismatic Vanguard", 10, Rarity.COMMON, mage.cards.c.CharismaticVanguard.class));
        cards.add(new SetCardInfo("Evolved Sleeper", 93, Rarity.RARE, mage.cards.e.EvolvedSleeper.class));
        cards.add(new SetCardInfo("Forest", 281, Rarity.LAND, mage.cards.basiclands.Forest.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Herd Migration", 429, Rarity.RARE, mage.cards.h.HerdMigration.class));
        cards.add(new SetCardInfo("Impede Momentum", 54, Rarity.COMMON, mage.cards.i.ImpedeMomentum.class));
        cards.add(new SetCardInfo("Island", 278, Rarity.LAND, mage.cards.basiclands.Island.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Jaya, Fiery Negotiator", 133, Rarity.MYTHIC, mage.cards.j.JayaFieryNegotiator.class));
        cards.add(new SetCardInfo("Karplusan Forest", 250, Rarity.RARE, mage.cards.k.KarplusanForest.class));
        cards.add(new SetCardInfo("Lightning Strike", 137, Rarity.COMMON, mage.cards.l.LightningStrike.class));
        cards.add(new SetCardInfo("Liliana of the Veil", 97, Rarity.MYTHIC, mage.cards.l.LilianaOfTheVeil.class));
        cards.add(new SetCardInfo("Llanowar Loamspeaker", 170, Rarity.RARE, mage.cards.l.LlanowarLoamspeaker.class));
        cards.add(new SetCardInfo("Magnigoth Sentry", 172, Rarity.COMMON, mage.cards.m.MagnigothSentry.class));
        cards.add(new SetCardInfo("Mesa Cavalier", 26, Rarity.COMMON, mage.cards.m.MesaCavalier.class));
        cards.add(new SetCardInfo("Micromancer", 57, Rarity.UNCOMMON, mage.cards.m.Micromancer.class));
        cards.add(new SetCardInfo("Mountain", 280, Rarity.LAND, mage.cards.basiclands.Mountain.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Nishoba Brawler", 174, Rarity.UNCOMMON, mage.cards.n.NishobaBrawler.class));
        cards.add(new SetCardInfo("Plains", 277, Rarity.LAND, mage.cards.basiclands.Plains.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Raff, Weatherlight Stalwart", 212, Rarity.UNCOMMON, mage.cards.r.RaffWeatherlightStalwart.class));
        cards.add(new SetCardInfo("Resolute Reinforcements", 29, Rarity.UNCOMMON, mage.cards.r.ResoluteReinforcements.class));
        cards.add(new SetCardInfo("Samite Herbalist", 31, Rarity.COMMON, mage.cards.s.SamiteHerbalist.class));
        cards.add(new SetCardInfo("Shalai's Acolyte", 33, Rarity.UNCOMMON, mage.cards.s.ShalaisAcolyte.class));
        cards.add(new SetCardInfo("Sheoldred, the Apocalypse", 107, Rarity.MYTHIC, mage.cards.s.SheoldredTheApocalypse.class));
        cards.add(new SetCardInfo("Shivan Devastator", 143, Rarity.MYTHIC, mage.cards.s.ShivanDevastator.class));
        cards.add(new SetCardInfo("Shivan Reef", 255, Rarity.RARE, mage.cards.s.ShivanReef.class));
        cards.add(new SetCardInfo("Snarespinner", 179, Rarity.COMMON, mage.cards.s.Snarespinner.class));
        cards.add(new SetCardInfo("Sulfurous Springs", 256, Rarity.RARE, mage.cards.s.SulfurousSprings.class));
        cards.add(new SetCardInfo("Swamp", 279, Rarity.LAND, mage.cards.basiclands.Swamp.class, FULL_ART_BFZ_VARIOUS));
        cards.add(new SetCardInfo("Tattered Apparition", 111, Rarity.COMMON, mage.cards.t.TatteredApparition.class));
        cards.add(new SetCardInfo("Temporal Firestorm", 147, Rarity.RARE, mage.cards.t.TemporalFirestorm.class));
        cards.add(new SetCardInfo("Territorial Maro", 184, Rarity.UNCOMMON, mage.cards.t.TerritorialMaro.class));
        cards.add(new SetCardInfo("Toxic Abomination", 112, Rarity.COMMON, mage.cards.t.ToxicAbomination.class));
        cards.add(new SetCardInfo("Viashino Branchrider", 150, Rarity.COMMON, mage.cards.v.ViashinoBranchrider.class));
        cards.add(new SetCardInfo("Yavimaya Coast", 261, Rarity.RARE, mage.cards.y.YavimayaCoast.class));
    }

//    @Override
//    public BoosterCollator createCollator() {
//        return new DominariaUnitedCollator();
//    }
}
