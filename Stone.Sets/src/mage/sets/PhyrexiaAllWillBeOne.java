package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 * @author TheElk801
 */
public final class PhyrexiaAllWillBeOne extends ExpansionSet {

    private static final PhyrexiaAllWillBeOne instance = new PhyrexiaAllWillBeOne();

    public static PhyrexiaAllWillBeOne getInstance() {
        return instance;
    }

    private PhyrexiaAllWillBeOne() {
        super("Phyrexia: All Will Be One", "ONE", ExpansionSet.buildDate(2023, 1, 10), SetType.EXPANSION);
        this.blockName = "Phyrexia: All Will Be One";
        this.hasBoosters = false; // temporary

        cards.add(new SetCardInfo("Annex Sentry", 2, Rarity.UNCOMMON, mage.cards.a.AnnexSentry.class));
        cards.add(new SetCardInfo("Anoint with Affliction", 81, Rarity.COMMON, mage.cards.a.AnointWithAffliction.class));
        cards.add(new SetCardInfo("Apostle of Invasion", 3, Rarity.UNCOMMON, mage.cards.a.ApostleOfInvasion.class));
        cards.add(new SetCardInfo("Archfiend of the Dross", 82, Rarity.RARE, mage.cards.a.ArchfiendOfTheDross.class));
        cards.add(new SetCardInfo("Argentum Masticore", 222, Rarity.RARE, mage.cards.a.ArgentumMasticore.class));
        cards.add(new SetCardInfo("Atraxa, Grand Unifier", 196, Rarity.MYTHIC, mage.cards.a.AtraxaGrandUnifier.class));
        cards.add(new SetCardInfo("Basilica Skullbomb", 224, Rarity.COMMON, mage.cards.b.BasilicaSkullbomb.class));
        cards.add(new SetCardInfo("Bilious Skulldweller", 83, Rarity.UNCOMMON, mage.cards.b.BiliousSkulldweller.class));
        cards.add(new SetCardInfo("Black Sun's Twilight", 84, Rarity.RARE, mage.cards.b.BlackSunsTwilight.class));
        cards.add(new SetCardInfo("Blackcleave Cliffs", 248, Rarity.RARE, mage.cards.b.BlackcleaveCliffs.class));
        cards.add(new SetCardInfo("Blightbelly Rat", 85, Rarity.COMMON, mage.cards.b.BlightbellyRat.class));
        cards.add(new SetCardInfo("Bloated Contaminator", 159, Rarity.RARE, mage.cards.b.BloatedContaminator.class));
        cards.add(new SetCardInfo("Blue Sun's Twilight", 43, Rarity.RARE, mage.cards.b.BlueSunsTwilight.class));
        cards.add(new SetCardInfo("Bonepicker Skirge", 86, Rarity.COMMON, mage.cards.b.BonepickerSkirge.class));
        cards.add(new SetCardInfo("Cacophony Scamp", 124, Rarity.UNCOMMON, mage.cards.c.CacophonyScamp.class));
        cards.add(new SetCardInfo("Chittering Skitterling", 87, Rarity.COMMON, mage.cards.c.ChitteringSkitterling.class));
        cards.add(new SetCardInfo("Copperline Gorge", 249, Rarity.RARE, mage.cards.c.CopperlineGorge.class));
        cards.add(new SetCardInfo("Darkslick Shores", 372, Rarity.RARE, mage.cards.d.DarkslickShores.class));
        cards.add(new SetCardInfo("Dragonwing Glider", 126, Rarity.RARE, mage.cards.d.DragonwingGlider.class));
        cards.add(new SetCardInfo("Dross Skullbomb", 225, Rarity.COMMON, mage.cards.d.DrossSkullbomb.class));
        cards.add(new SetCardInfo("Elesh Norn, Mother of Machines", 10, Rarity.MYTHIC, mage.cards.e.EleshNornMotherOfMachines.class));
        cards.add(new SetCardInfo("Encroaching Mycosynth", 47, Rarity.RARE, mage.cards.e.EncroachingMycosynth.class));
        cards.add(new SetCardInfo("Evolved Spinoderm", 166, Rarity.RARE, mage.cards.e.EvolvedSpinoderm.class));
        cards.add(new SetCardInfo("Experimental Augury", 49, Rarity.COMMON, mage.cards.e.ExperimentalAugury.class));
        cards.add(new SetCardInfo("Ezuri, Stalker of Spheres", 201, Rarity.RARE, mage.cards.e.EzuriStalkerOfSpheres.class));
        cards.add(new SetCardInfo("Feed the Infection", 93, Rarity.UNCOMMON, mage.cards.f.FeedTheInfection.class));
        cards.add(new SetCardInfo("Forest", 276, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Gleeful Demolition", 134, Rarity.UNCOMMON, mage.cards.g.GleefulDemolition.class));
        cards.add(new SetCardInfo("Hexgold Slash", 137, Rarity.COMMON, mage.cards.h.HexgoldSlash.class));
        cards.add(new SetCardInfo("Furnace Skullbomb", 228, Rarity.COMMON, mage.cards.f.FurnaceSkullbomb.class));
        cards.add(new SetCardInfo("Goliath Hatchery", 408, Rarity.RARE, mage.cards.g.GoliathHatchery.class));
        cards.add(new SetCardInfo("Graaz, Unstoppable Juggernaut", 229, Rarity.RARE, mage.cards.g.GraazUnstoppableJuggernaut.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Graaz, Unstoppable Juggernaut", 334, Rarity.RARE, mage.cards.g.GraazUnstoppableJuggernaut.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Hexgold Halberd", 136, Rarity.UNCOMMON, mage.cards.h.HexgoldHalberd.class));
        cards.add(new SetCardInfo("Incisor Glider", 15, Rarity.COMMON, mage.cards.i.IncisorGlider.class));
        cards.add(new SetCardInfo("Infested Fleshcutter", 17, Rarity.UNCOMMON, mage.cards.i.InfestedFleshcutter.class));
        cards.add(new SetCardInfo("Island", 273, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Jawbone Duelist", 18, Rarity.UNCOMMON, mage.cards.j.JawboneDuelist.class));
        cards.add(new SetCardInfo("Jor Kadeen, First Goldwarden", 203, Rarity.RARE, mage.cards.j.JorKadeenFirstGoldwarden.class));
        cards.add(new SetCardInfo("Karumonix, the Rat King", 98, Rarity.RARE, mage.cards.k.KarumonixTheRatKing.class));
        cards.add(new SetCardInfo("Kaya, Intangible Slayer", 205, Rarity.RARE, mage.cards.k.KayaIntangibleSlayer.class));
        cards.add(new SetCardInfo("Kemba, Kha Enduring", 19, Rarity.RARE, mage.cards.k.KembaKhaEnduring.class));
        cards.add(new SetCardInfo("Koth, Fire of Resistance", 138, Rarity.RARE, mage.cards.k.KothFireOfResistance.class));
        cards.add(new SetCardInfo("Malcator, Purity Overseer", 208, Rarity.RARE, mage.cards.m.MalcatorPurityOverseer.class));
        cards.add(new SetCardInfo("Maze Skullbomb", 231, Rarity.COMMON, mage.cards.m.MazeSkullbomb.class));
        cards.add(new SetCardInfo("Mercurial Spelldancer", 61, Rarity.RARE, mage.cards.m.MercurialSpelldancer.class));
        cards.add(new SetCardInfo("Mesmerizing Dose", 62, Rarity.COMMON, mage.cards.m.MesmerizingDose.class));
        cards.add(new SetCardInfo("Migloz, Maze Crusher", 210, Rarity.RARE, mage.cards.m.MiglozMazeCrusher.class));
        cards.add(new SetCardInfo("Mindsplice Apparatus", 63, Rarity.RARE, mage.cards.m.MindspliceApparatus.class));
        cards.add(new SetCardInfo("Minor Misstep", 64, Rarity.UNCOMMON, mage.cards.m.MinorMisstep.class));
        cards.add(new SetCardInfo("Mirran Safehouse", 232, Rarity.RARE, mage.cards.m.MirranSafehouse.class));
        cards.add(new SetCardInfo("Mirrex", 254, Rarity.RARE, mage.cards.m.Mirrex.class));
        cards.add(new SetCardInfo("Mite Overseer", 404, Rarity.RARE, mage.cards.m.MiteOverseer.class));
        cards.add(new SetCardInfo("Mondrak, Glory Dominus", 23, Rarity.MYTHIC, mage.cards.m.MondrakGloryDominus.class));
        cards.add(new SetCardInfo("Monument to Perfection", 233, Rarity.RARE, mage.cards.m.MonumentToPerfection.class));
        cards.add(new SetCardInfo("Mountain", 275, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Myr Convert", 234, Rarity.UNCOMMON, mage.cards.m.MyrConvert.class));
        cards.add(new SetCardInfo("Necrogen Communion", 99, Rarity.UNCOMMON, mage.cards.n.NecrogenCommunion.class));
        cards.add(new SetCardInfo("Necrogen Rotpriest", 212, Rarity.UNCOMMON, mage.cards.n.NecrogenRotpriest.class));
        cards.add(new SetCardInfo("Nimraiser Paladin", 101, Rarity.UNCOMMON, mage.cards.n.NimraiserPaladin.class));
        cards.add(new SetCardInfo("Nissa, Ascended Animist", 175, Rarity.MYTHIC, mage.cards.n.NissaAscendedAnimist.class));
        cards.add(new SetCardInfo("Norn's Wellspring", 24, Rarity.RARE, mage.cards.n.NornsWellspring.class));
        cards.add(new SetCardInfo("Ossification", 26, Rarity.UNCOMMON, mage.cards.o.Ossification.class));
        cards.add(new SetCardInfo("Ovika, Enigma Goliath", 213, Rarity.RARE, mage.cards.o.OvikaEnigmaGoliath.class));
        cards.add(new SetCardInfo("Oxidda Finisher", 143, Rarity.UNCOMMON, mage.cards.o.OxiddaFinisher.class));
        cards.add(new SetCardInfo("Paladin of Predation", 178, Rarity.UNCOMMON, mage.cards.p.PaladinOfPredation.class));
        cards.add(new SetCardInfo("Phyrexian Arena", 104, Rarity.RARE, mage.cards.p.PhyrexianArena.class));
        cards.add(new SetCardInfo("Phyrexian Obliterator", 105, Rarity.MYTHIC, mage.cards.p.PhyrexianObliterator.class));
        cards.add(new SetCardInfo("Plains", 272, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Planar Disruption", 28, Rarity.COMMON, mage.cards.p.PlanarDisruption.class));
        cards.add(new SetCardInfo("Prosthetic Injector", 239, Rarity.UNCOMMON, mage.cards.p.ProstheticInjector.class));
        cards.add(new SetCardInfo("Razorverge Thicket", 257, Rarity.RARE, mage.cards.r.RazorvergeThicket.class));
        cards.add(new SetCardInfo("Resistance Reunited", 31, Rarity.UNCOMMON, mage.cards.r.ResistanceReunited.class));
        cards.add(new SetCardInfo("Resistance Skywarden", 146, Rarity.UNCOMMON, mage.cards.r.ResistanceSkywarden.class));
        cards.add(new SetCardInfo("Sawblade Scamp", 147, Rarity.COMMON, mage.cards.s.SawbladeScamp.class));
        cards.add(new SetCardInfo("Scheming Aspirant", 107, Rarity.UNCOMMON, mage.cards.s.SchemingAspirant.class));
        cards.add(new SetCardInfo("Seachrome Coast", 258, Rarity.RARE, mage.cards.s.SeachromeCoast.class));
        cards.add(new SetCardInfo("Serum Sovereign", 405, Rarity.RARE, mage.cards.s.SerumSovereign.class));
        cards.add(new SetCardInfo("Sheoldred's Edict", 108, Rarity.UNCOMMON, mage.cards.s.SheoldredsEdict.class));
        cards.add(new SetCardInfo("Sinew Dancer", 32, Rarity.COMMON, mage.cards.s.SinewDancer.class));
        cards.add(new SetCardInfo("Skrelv's Hive", 34, Rarity.RARE, mage.cards.s.SkrelvsHive.class));
        cards.add(new SetCardInfo("Slaughter Singer", 216, Rarity.UNCOMMON, mage.cards.s.SlaughterSinger.class));
        cards.add(new SetCardInfo("Staff of Compleation", 242, Rarity.MYTHIC, mage.cards.s.StaffOfCompleation.class));
        cards.add(new SetCardInfo("Surgical Skullbomb", 243, Rarity.COMMON, mage.cards.s.SurgicalSkullbomb.class));
        cards.add(new SetCardInfo("Swamp", 274, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swooping Lookout", 35, Rarity.UNCOMMON, mage.cards.s.SwoopingLookout.class));
        cards.add(new SetCardInfo("Tablet of Compleation", 245, Rarity.RARE, mage.cards.t.TabletOfCompleation.class));
        cards.add(new SetCardInfo("Tamiyo's Immobilizer", 69, Rarity.UNCOMMON, mage.cards.t.TamiyosImmobilizer.class));
        cards.add(new SetCardInfo("The Filigree Sylex", 227, Rarity.RARE, mage.cards.t.TheFiligreeSylex.class));
        cards.add(new SetCardInfo("The Monumental Facade", 255, Rarity.RARE, mage.cards.t.TheMonumentalFacade.class));
        cards.add(new SetCardInfo("The Seedcore", 259, Rarity.RARE, mage.cards.t.TheSeedcore.class));
        cards.add(new SetCardInfo("Thrun, Breaker of Silence", 186, Rarity.RARE, mage.cards.t.ThrunBreakerOfSilence.class));
        cards.add(new SetCardInfo("Transplant Theorist", 73, Rarity.UNCOMMON, mage.cards.t.TransplantTheorist.class));
        cards.add(new SetCardInfo("Tyrranax Rex", 189, Rarity.MYTHIC, mage.cards.t.TyrranaxRex.class));
        cards.add(new SetCardInfo("Tyvar's Stand", 190, Rarity.UNCOMMON, mage.cards.t.TyvarsStand.class));
        cards.add(new SetCardInfo("Tyvar, Jubilant Brawler", 218, Rarity.RARE, mage.cards.t.TyvarJubilantBrawler.class));
        cards.add(new SetCardInfo("Unctus, Grand Metatect", 75, Rarity.RARE, mage.cards.u.UnctusGrandMetatect.class));
        cards.add(new SetCardInfo("Unnatural Restoration", 191, Rarity.UNCOMMON, mage.cards.u.UnnaturalRestoration.class));
        cards.add(new SetCardInfo("Urabrask's Anointer", 152, Rarity.UNCOMMON, mage.cards.u.UrabrasksAnointer.class));
        cards.add(new SetCardInfo("Urabrask's Forge", 153, Rarity.RARE, mage.cards.u.UrabrasksForge.class));
        cards.add(new SetCardInfo("Vanish into Eternity", 36, Rarity.COMMON, mage.cards.v.VanishIntoEternity.class));
        cards.add(new SetCardInfo("Venerated Rotpriest", 192, Rarity.RARE, mage.cards.v.VeneratedRotpriest.class));
        cards.add(new SetCardInfo("Venser, Corpse Puppet", 219, Rarity.RARE, mage.cards.v.VenserCorpsePuppet.class));
        cards.add(new SetCardInfo("Vindictive Flamestoker", 154, Rarity.RARE, mage.cards.v.VindictiveFlamestoker.class));
        cards.add(new SetCardInfo("Volt Charge", 155, Rarity.COMMON, mage.cards.v.VoltCharge.class));
        cards.add(new SetCardInfo("Vraan, Executioner Thane", 114, Rarity.COMMON, mage.cards.v.VraanExecutionerThane.class));
        cards.add(new SetCardInfo("Vulshok Splitter", 156, Rarity.COMMON, mage.cards.v.VulshokSplitter.class));
        cards.add(new SetCardInfo("White Sun's Twilight", 38, Rarity.RARE, mage.cards.w.WhiteSunsTwilight.class));
    }

//    @Override
//    public BoosterCollator createCollator() {
//        return new PhyrexiaAllWillBeOneCollator();
//    }
}
