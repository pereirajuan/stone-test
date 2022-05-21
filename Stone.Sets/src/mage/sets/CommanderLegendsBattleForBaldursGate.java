package mage.sets;

import mage.cards.ExpansionSet;
import mage.constants.Rarity;
import mage.constants.SetType;

/**
 * @author TheElk801
 */
public final class CommanderLegendsBattleForBaldursGate extends ExpansionSet {

    private static final CommanderLegendsBattleForBaldursGate instance = new CommanderLegendsBattleForBaldursGate();

    public static CommanderLegendsBattleForBaldursGate getInstance() {
        return instance;
    }

    private CommanderLegendsBattleForBaldursGate() {
        super("Commander Legends: Battle for Baldur's Gate", "CLB", ExpansionSet.buildDate(2022, 6, 10), SetType.SUPPLEMENTAL);
        this.blockName = "Commander Legends";
        this.hasBasicLands = true;
        this.hasBoosters = false; // temporary

        cards.add(new SetCardInfo("Aarakocra Sneak", 54, Rarity.COMMON, mage.cards.a.AarakocraSneak.class));
        cards.add(new SetCardInfo("Agent of the Iron Throne", 107, Rarity.UNCOMMON, mage.cards.a.AgentOfTheIronThrone.class));
        cards.add(new SetCardInfo("Ancient Brass Dragon", 111, Rarity.MYTHIC, mage.cards.a.AncientBrassDragon.class));
        cards.add(new SetCardInfo("Arcane Signet", 298, Rarity.UNCOMMON, mage.cards.a.ArcaneSignet.class));
        cards.add(new SetCardInfo("Astarion, the Decadent", 265, Rarity.RARE, mage.cards.a.AstarionTheDecadent.class));
        cards.add(new SetCardInfo("Astral Confrontation", 6, Rarity.COMMON, mage.cards.a.AstralConfrontation.class));
        cards.add(new SetCardInfo("Avenging Hunter", 215, Rarity.COMMON, mage.cards.a.AvengingHunter.class));
        cards.add(new SetCardInfo("Baba Lysaga, Night Witch", 266, Rarity.RARE, mage.cards.b.BabaLysagaNightWitch.class));
        cards.add(new SetCardInfo("Baldur's Gate", 345, Rarity.RARE, mage.cards.b.BaldursGate.class));
        cards.add(new SetCardInfo("Bane's Invoker", 7, Rarity.COMMON, mage.cards.b.BanesInvoker.class));
        cards.add(new SetCardInfo("Basilisk Collar", 300, Rarity.RARE, mage.cards.b.BasiliskCollar.class));
        cards.add(new SetCardInfo("Battle Angels of Tyr", 9, Rarity.MYTHIC, mage.cards.b.BattleAngelsOfTyr.class));
        cards.add(new SetCardInfo("Bhaal's Invoker", 163, Rarity.COMMON, mage.cards.b.BhaalsInvoker.class));
        cards.add(new SetCardInfo("Blur", 58, Rarity.COMMON, mage.cards.b.Blur.class));
        cards.add(new SetCardInfo("Bountiful Promenade", 348, Rarity.RARE, mage.cards.b.BountifulPromenade.class));
        cards.add(new SetCardInfo("Bramble Sovereign", 218, Rarity.MYTHIC, mage.cards.b.BrambleSovereign.class));
        cards.add(new SetCardInfo("Cadira, Caller of the Small", 269, Rarity.UNCOMMON, mage.cards.c.CadiraCallerOfTheSmall.class));
        cards.add(new SetCardInfo("Charcoal Diamond", 305, Rarity.COMMON, mage.cards.c.CharcoalDiamond.class));
        cards.add(new SetCardInfo("Cloakwood Hermit", 221, Rarity.UNCOMMON, mage.cards.c.CloakwoodHermit.class));
        cards.add(new SetCardInfo("Command Tower", 351, Rarity.COMMON, mage.cards.c.CommandTower.class));
        cards.add(new SetCardInfo("Criminal Past", 122, Rarity.UNCOMMON, mage.cards.c.CriminalPast.class));
        cards.add(new SetCardInfo("Cultist of the Absolute", 123, Rarity.RARE, mage.cards.c.CultistOfTheAbsolute.class));
        cards.add(new SetCardInfo("Dread Linnorm", 225, Rarity.COMMON, mage.cards.d.DreadLinnorm.class));
        cards.add(new SetCardInfo("Duke Ulder Ravengard", 272, Rarity.RARE, mage.cards.d.DukeUlderRavengard.class));
        cards.add(new SetCardInfo("Dungeon Delver", 67, Rarity.UNCOMMON, mage.cards.d.DungeonDelver.class));
        cards.add(new SetCardInfo("Dungeoneer's Pack", 312, Rarity.UNCOMMON, mage.cards.d.DungeoneersPack.class));
        cards.add(new SetCardInfo("Elder Brain", 125, Rarity.RARE, mage.cards.e.ElderBrain.class));
        cards.add(new SetCardInfo("Ellyn Harbreeze, Busybody", 16, Rarity.UNCOMMON, mage.cards.e.EllynHarbreezeBusybody.class));
        cards.add(new SetCardInfo("Elminster", 274, Rarity.MYTHIC, mage.cards.e.Elminster.class));
        cards.add(new SetCardInfo("Elminster's Simulacrum", 561, Rarity.MYTHIC, mage.cards.e.ElminstersSimulacrum.class));
        cards.add(new SetCardInfo("Erinis, Gloom Stalker", 230, Rarity.UNCOMMON, mage.cards.e.ErinisGloomStalker.class));
        cards.add(new SetCardInfo("Faceless One", 1, Rarity.COMMON, mage.cards.f.FacelessOne.class));
        cards.add(new SetCardInfo("Fang Dragon", 173, Rarity.COMMON, mage.cards.f.FangDragon.class));
        cards.add(new SetCardInfo("Far Traveler", 17, Rarity.UNCOMMON, mage.cards.f.FarTraveler.class));
        cards.add(new SetCardInfo("Firbolg Flutist", 174, Rarity.RARE, mage.cards.f.FirbolgFlutist.class));
        cards.add(new SetCardInfo("Fire Diamond", 313, Rarity.COMMON, mage.cards.f.FireDiamond.class));
        cards.add(new SetCardInfo("Fireball", 175, Rarity.UNCOMMON, mage.cards.f.Fireball.class));
        cards.add(new SetCardInfo("Flaming Fist", 18, Rarity.COMMON, mage.cards.f.FlamingFist.class));
        cards.add(new SetCardInfo("Forest", 467, Rarity.LAND, mage.cards.basiclands.Forest.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Geode Golem", 316, Rarity.UNCOMMON, mage.cards.g.GeodeGolem.class));
        cards.add(new SetCardInfo("Goggles of Night", 74, Rarity.COMMON, mage.cards.g.GogglesOfNight.class));
        cards.add(new SetCardInfo("Gorion, Wise Mentor", 276, Rarity.RARE, mage.cards.g.GorionWiseMentor.class));
        cards.add(new SetCardInfo("Imoen, Mystic Trickster", 77, Rarity.UNCOMMON, mage.cards.i.ImoenMysticTrickster.class));
        cards.add(new SetCardInfo("Irenicus's Vile Duplication", 78, Rarity.UNCOMMON, mage.cards.i.IrenicussVileDuplication.class));
        cards.add(new SetCardInfo("Island", 455, Rarity.LAND, mage.cards.basiclands.Island.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Jaheira's Respite", 238, Rarity.RARE, mage.cards.j.JaheirasRespite.class));
        cards.add(new SetCardInfo("Jaheira, Friend of the Forest", 237, Rarity.RARE, mage.cards.j.JaheiraFriendOfTheForest.class));
        cards.add(new SetCardInfo("Karlach, Fury of Avernus", 186, Rarity.MYTHIC, mage.cards.k.KarlachFuryOfAvernus.class));
        cards.add(new SetCardInfo("Korlessa, Scale Singer", 280, Rarity.UNCOMMON, mage.cards.k.KorlessaScaleSinger.class));
        cards.add(new SetCardInfo("Lae'zel, Vlaakith's Champion", 29, Rarity.RARE, mage.cards.l.LaezelVlaakithsChampion.class));
        cards.add(new SetCardInfo("Lightning Bolt", 187, Rarity.COMMON, mage.cards.l.LightningBolt.class));
        cards.add(new SetCardInfo("Livaan, Cultist of Tiamat", 188, Rarity.UNCOMMON, mage.cards.l.LivaanCultistOfTiamat.class));
        cards.add(new SetCardInfo("Lulu, Loyal Hollyphant", 32, Rarity.UNCOMMON, mage.cards.l.LuluLoyalHollyphant.class));
        cards.add(new SetCardInfo("Luxury Suite", 355, Rarity.RARE, mage.cards.l.LuxurySuite.class));
        cards.add(new SetCardInfo("Mahadi, Emporium Master", 282, Rarity.UNCOMMON, mage.cards.m.MahadiEmporiumMaster.class));
        cards.add(new SetCardInfo("Marble Diamond", 320, Rarity.COMMON, mage.cards.m.MarbleDiamond.class));
        cards.add(new SetCardInfo("Minsc & Boo, Timeless Heroes", 285, Rarity.MYTHIC, mage.cards.m.MinscBooTimelessHeroes.class));
        cards.add(new SetCardInfo("Monster Manual", 242, Rarity.RARE, mage.cards.m.MonsterManual.class));
        cards.add(new SetCardInfo("Morphic Pool", 357, Rarity.RARE, mage.cards.m.MorphicPool.class));
        cards.add(new SetCardInfo("Moss Diamond", 327, Rarity.COMMON, mage.cards.m.MossDiamond.class));
        cards.add(new SetCardInfo("Mountain", 463, Rarity.LAND, mage.cards.basiclands.Mountain.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Nalia de'Arnise", 649, Rarity.MYTHIC, mage.cards.n.NaliaDeArnise.class));
        cards.add(new SetCardInfo("Nautiloid Ship", 328, Rarity.MYTHIC, mage.cards.n.NautiloidShip.class));
        cards.add(new SetCardInfo("Nemesis Phoenix", 189, Rarity.UNCOMMON, mage.cards.n.NemesisPhoenix.class));
        cards.add(new SetCardInfo("Noble's Purse", 331, Rarity.UNCOMMON, mage.cards.n.NoblesPurse.class));
        cards.add(new SetCardInfo("Oji, the Exquisite Blade", 290, Rarity.UNCOMMON, mage.cards.o.OjiTheExquisiteBlade.class));
        cards.add(new SetCardInfo("Passageway Seer", 141, Rarity.UNCOMMON, mage.cards.p.PassagewaySeer.class));
        cards.add(new SetCardInfo("Plains", 451, Rarity.LAND, mage.cards.basiclands.Plains.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Raised by Giants", 250, Rarity.RARE, mage.cards.r.RaisedByGiants.class));
        cards.add(new SetCardInfo("Raphael, Fiendish Savior", 292, Rarity.RARE, mage.cards.r.RaphaelFiendishSavior.class));
        cards.add(new SetCardInfo("Reflecting Pool", 358, Rarity.RARE, mage.cards.r.ReflectingPool.class));
        cards.add(new SetCardInfo("Roving Harper", 40, Rarity.COMMON, mage.cards.r.RovingHarper.class));
        cards.add(new SetCardInfo("Sarevok, Deathbringer", 144, Rarity.UNCOMMON, mage.cards.s.SarevokDeathbringer.class));
        cards.add(new SetCardInfo("Sea Hag", 95, Rarity.COMMON, mage.cards.s.SeaHag.class));
        cards.add(new SetCardInfo("Sea of Clouds", 360, Rarity.RARE, mage.cards.s.SeaOfClouds.class));
        cards.add(new SetCardInfo("Shadowheart, Dark Justiciar", 146, Rarity.RARE, mage.cards.s.ShadowheartDarkJusticiar.class));
        cards.add(new SetCardInfo("Shameless Charlatan", 96, Rarity.RARE, mage.cards.s.ShamelessCharlatan.class));
        cards.add(new SetCardInfo("Silvanus's Invoker", 254, Rarity.COMMON, mage.cards.s.SilvanussInvoker.class));
        cards.add(new SetCardInfo("Sky Diamond", 337, Rarity.COMMON, mage.cards.s.SkyDiamond.class));
        cards.add(new SetCardInfo("Spire Garden", 361, Rarity.RARE, mage.cards.s.SpireGarden.class));
        cards.add(new SetCardInfo("Stirge", 150, Rarity.COMMON, mage.cards.s.Stirge.class));
        cards.add(new SetCardInfo("Summon Undead", 151, Rarity.COMMON, mage.cards.s.SummonUndead.class));
        cards.add(new SetCardInfo("Swamp", 459, Rarity.LAND, mage.cards.basiclands.Swamp.class, NON_FULL_USE_VARIOUS));
        cards.add(new SetCardInfo("Swiftfoot Boots", 339, Rarity.UNCOMMON, mage.cards.s.SwiftfootBoots.class));
        cards.add(new SetCardInfo("Tasha, the Witch Queen", 294, Rarity.MYTHIC, mage.cards.t.TashaTheWitchQueen.class));
        cards.add(new SetCardInfo("The Council of Four", 271, Rarity.RARE, mage.cards.t.TheCouncilOfFour.class));
        cards.add(new SetCardInfo("Treasure Keeper", 341, Rarity.UNCOMMON, mage.cards.t.TreasureKeeper.class));
        cards.add(new SetCardInfo("Tymora's Invoker", 101, Rarity.COMMON, mage.cards.t.TymorasInvoker.class));
        cards.add(new SetCardInfo("Viconia, Drow Apostate", 156, Rarity.UNCOMMON, mage.cards.v.ViconiaDrowApostate.class));
        cards.add(new SetCardInfo("Wand of Wonder", 204, Rarity.RARE, mage.cards.w.WandOfWonder.class));
        cards.add(new SetCardInfo("Wayfarer's Bauble", 344, Rarity.COMMON, mage.cards.w.WayfarersBauble.class));
        cards.add(new SetCardInfo("White Plume Adventurer", 49, Rarity.RARE, mage.cards.w.WhitePlumeAdventurer.class));
        cards.add(new SetCardInfo("Wilson, Refined Grizzly", 261, Rarity.UNCOMMON, mage.cards.w.WilsonRefinedGrizzly.class));
        cards.add(new SetCardInfo("Wyll, Blade of Frontiers", 208, Rarity.RARE, mage.cards.w.WyllBladeOfFrontiers.class));
        cards.add(new SetCardInfo("Zevlor, Elturel Exile", 296, Rarity.RARE, mage.cards.z.ZevlorElturelExile.class));
    }
}
