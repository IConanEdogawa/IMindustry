package imindustry;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.content.*;
import mindustry.entities.Damage;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.mod.Mod;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;

import static mindustry.Vars.tilesize;

public class IMindustry extends Mod {

    public static StatusEffect radiation;

    private static final ObjectMap<Building, Float> hailHeat = new ObjectMap<>();
    private static final ObjectMap<Building, Integer> hailShots = new ObjectMap<>();

    @Override
    public void loadContent() {

        // === Radiation status ===
        radiation = new StatusEffect("im-radiation"){{
            color = Pal.reactorPurple;
            damage = 0.09f;
            transitionDamage = 4f;
            effect = Fx.reactorsmoke;
            effectChance = 0.025f;
        }};

        // ===================== DUO =====================
        ItemTurret duo = (ItemTurret) Blocks.duo;
        BasicBulletType duoSilicon = (BasicBulletType) duo.ammoTypes.get(Items.silicon);
        float duoRange = duo.range;

        BasicBulletType duoTitanium = new BasicBulletType(4.5f, duoSilicon.damage * 2f);
        duoTitanium.width = 7f;
        duoTitanium.height = 10f;
        duoTitanium.lifetime = 40f;
        duoTitanium.reloadMultiplier = 0.5f;
        duoTitanium.status = StatusEffects.slow;
        duoTitanium.statusDuration = 90f;
        duoTitanium.rangeOverride = duoRange + 16f;
        duoTitanium.hitColor = Color.valueOf("8da1e3");
        duoTitanium.frontColor = Color.valueOf("8da1e3");
        duoTitanium.backColor = Color.valueOf("5c6ea3");

        BasicBulletType duoSand = new BasicBulletType(4f, duoSilicon.damage * 0.6f);
        duoSand.lifetime = 35f;
        duoSand.knockback = 1.4f;
        duoSand.rangeOverride = duoRange + 16f;
        duoSand.hitColor = Color.valueOf("f7cba4");
        duoSand.frontColor = Color.valueOf("f7cba4");
        duoSand.backColor = Color.valueOf("c9986f");

        duo.ammoTypes.put(Items.titanium, duoTitanium);
        duo.ammoTypes.put(Items.sand, duoSand);

        // ===================== SCATTER =====================
        BasicBulletType scatterFrag = new BasicBulletType(3.5f, 6f);
        scatterFrag.lifetime = 18f;
        scatterFrag.width = 3f;
        scatterFrag.height = 4f;
        scatterFrag.collidesGround = false;

        BasicBulletType scatterSilicon = new BasicBulletType(5.5f, 10f);
        scatterSilicon.width = 6f;
        scatterSilicon.height = 8f;
        scatterSilicon.lifetime = 30f;
        scatterSilicon.homingPower = 0.25f;
        scatterSilicon.homingRange = 70f;
        scatterSilicon.collidesGround = false;
        scatterSilicon.fragBullets = 6;
        scatterSilicon.fragBullet = scatterFrag;
        scatterSilicon.fragSpread = 60f;

        ((ItemTurret) Blocks.scatter).ammoTypes.put(Items.silicon, scatterSilicon);

        // ===================== HAIL =====================
        ItemTurret hail = (ItemTurret) Blocks.hail;

        // Keep original ammo intact (so damage shows correctly)
        ArtilleryBulletType originalGraphite = (ArtilleryBulletType) hail.ammoTypes.get(Items.graphite);
        ArtilleryBulletType originalSilicon = (ArtilleryBulletType) hail.ammoTypes.get(Items.silicon);

        float baseDamage = originalGraphite.damage;
        float baseSplash = originalGraphite.splashDamage;
        float baseSplashRad = originalGraphite.splashDamageRadius;

        // Titanium - faster + freeze
        ArtilleryBulletType hailTitanium = new ArtilleryBulletType(3.2f, baseDamage * 1.2f);
        hailTitanium.splashDamage = baseSplash * 1.15f;
        hailTitanium.splashDamageRadius = baseSplashRad;
        hailTitanium.width = 12f;
        hailTitanium.height = 12f;
        hailTitanium.lifetime = 70f;
        hailTitanium.reloadMultiplier = 1.5f;
        hailTitanium.status = StatusEffects.freezing;
        hailTitanium.statusDuration = 130f;
        hailTitanium.hitColor = Color.valueOf("8da1e3");
        hailTitanium.frontColor = Color.valueOf("8da1e3");
        hailTitanium.backColor = Color.valueOf("5c6ea3");
        hailTitanium.trailColor = Color.valueOf("8da1e3");
        hailTitanium.hitEffect = Fx.hitBulletColor;
        hailTitanium.despawnEffect = Fx.hitBulletColor;

        // Thorium - radiation + death damage (no visual boom)
        ArtilleryBulletType hailThorium = new ArtilleryBulletType(3.0f, baseDamage * 1.25f){
            @Override
            public void hit(Bullet b, float x, float y){
                super.hit(b, x, y);
                if(b.owner instanceof Building build){
                    int shots = hailShots.get(build, 0) + 1;
                    hailShots.put(build, shots);
                    hailHeat.put(build, 1f);
                }
            }
        };
        hailThorium.splashDamage = baseSplash * 1.2f;
        hailThorium.splashDamageRadius = baseSplashRad;
        hailThorium.width = 13f;
        hailThorium.height = 13f;
        hailThorium.lifetime = 75f;
        hailThorium.ammoMultiplier = 2f;
        hailThorium.reloadMultiplier = 1f;
        hailThorium.status = radiation;
        hailThorium.statusDuration = 60f * 8f;
        hailThorium.hitColor = Pal.reactorPurple;
        hailThorium.frontColor = Pal.reactorPurple;
        hailThorium.backColor = Pal.reactorPurple2;
        hailThorium.trailColor = Pal.reactorPurple;
        hailThorium.hitEffect = Fx.hitBulletColor;
        hailThorium.despawnEffect = Fx.hitBulletColor;

        hail.ammoTypes.put(Items.titanium, hailTitanium);
        hail.ammoTypes.put(Items.thorium, hailThorium);

        // Death damage only (no visual explosion effect)
        Events.on(UnitDestroyEvent.class, e -> {
            Unit unit = e.unit;
            if(unit == null || !unit.hasEffect(radiation)) return;

            float intensity = Mathf.clamp(unit.getDuration(radiation) / (60f * 8f), 0.2f, 1f);

            float damage = 100f + 350f * intensity; // 100-450
            float radius = 2.05f * tilesize;        // ~2 blocks

            Damage.damage(unit.team, unit.x, unit.y, radius, damage, true, true);
            // no Fx.reactorExplosion / no big smoke
        });

        // ===================== SCORCH (Oil mode) =====================
        // Scorch is a LiquidTurret. We add oil as a special ammo that fires projectiles + fire puddles.

        LiquidTurret scorch = (LiquidTurret) Blocks.scorch;

        // Create a projectile-style bullet for oil mode
        BasicBulletType scorchOilBullet = new BasicBulletType(4.8f, 18f){{
            lifetime = 28f;
            width = 10f;
            height = 12f;
            ammoMultiplier = 1f;
            status = StatusEffects.burning;
            statusDuration = 60f * 4f;
            hitEffect = Fx.hitFlameSmall;
            despawnEffect = Fx.fireSmoke;
            frontColor = Color.valueOf("ffb380");
            backColor = Color.valueOf("d06b3a");
            trailColor = Color.valueOf("ff8c42");

            // Leave fire on hit
            incendChance = 0.9f;
            incendSpread = 2.5f;
            incendAmount = 2;
        }};

        // Make oil shoot this bullet instead of the default continuous flame behavior
        // Note: LiquidTurret uses liquid as ammo, so we put it into ammoTypes
        scorch.ammoTypes.put(Liquids.oil, scorchOilBullet);

        // Slightly faster fire rate feel when using oil (Duo + 25% approximation via reloadMultiplier)
        scorchOilBullet.reloadMultiplier = 1.25f;
    }

    @Override
    public void init(){
        Events.run(mindustry.game.EventType.Trigger.update, () -> {
            hailHeat.each((build, heat) -> {
                if(build == null || !build.isValid()){
                    hailHeat.remove(build);
                    hailShots.remove(build);
                    return;
                }
                float newHeat = heat - Time.delta / (60f * 5f);
                if(newHeat <= 0f){
                    hailHeat.remove(build);
                    hailShots.remove(build);
                }else{
                    hailHeat.put(build, newHeat);
                }
            });
        });
    }
}
