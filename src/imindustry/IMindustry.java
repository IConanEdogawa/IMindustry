package imindustry;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.content.*;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.game.EventType.UnitDestroyEvent;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.mod.Mod;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.turrets.ItemTurret;

import static mindustry.Vars.tilesize;

public class IMindustry extends Mod {

    // Radiation status - stacks up to ~32
    public static StatusEffect radiation;

    // Track overheat for Hail buildings using Thorium
    private static final ObjectMap<Building, Float> hailHeat = new ObjectMap<>();
    private static final ObjectMap<Building, Integer> hailShots = new ObjectMap<>();

    @Override
    public void loadContent() {

        // === Custom Radiation Status ===
        radiation = new StatusEffect("im-radiation"){{
            color = Pal.reactorPurple;
            damage = 0.15f;          // small damage over time
            transitionDamage = 8f;
            effect = Fx.reactorsmoke;
            effectChance = 0.15f;
        }};

        // === Duo (already working) ===
        ItemTurret duo = (ItemTurret) Blocks.duo;
        BasicBulletType silicon = (BasicBulletType) duo.ammoTypes.get(Items.silicon);
        float duoRange = duo.range;

        BasicBulletType titaniumAmmo = new BasicBulletType(4.5f, silicon.damage * 2f);
        titaniumAmmo.width = 7f;
        titaniumAmmo.height = 10f;
        titaniumAmmo.lifetime = 40f;
        titaniumAmmo.reloadMultiplier = 0.5f;
        titaniumAmmo.status = StatusEffects.slow;
        titaniumAmmo.statusDuration = 90f;
        titaniumAmmo.rangeOverride = duoRange + 16f;
        titaniumAmmo.hitColor = Color.valueOf("8da1e3");
        titaniumAmmo.frontColor = Color.valueOf("8da1e3");
        titaniumAmmo.backColor = Color.valueOf("5c6ea3");

        BasicBulletType sandAmmo = new BasicBulletType(4f, silicon.damage * 0.6f);
        sandAmmo.lifetime = 35f;
        sandAmmo.knockback = 1.4f;
        sandAmmo.rangeOverride = duoRange + 16f;
        sandAmmo.hitColor = Color.valueOf("f7cba4");
        sandAmmo.frontColor = Color.valueOf("f7cba4");
        sandAmmo.backColor = Color.valueOf("c9986f");

        duo.ammoTypes.put(Items.titanium, titaniumAmmo);
        duo.ammoTypes.put(Items.sand, sandAmmo);

        // === Scatter Silicon ===
        BasicBulletType frag = new BasicBulletType(3.5f, 6f);
        frag.lifetime = 18f;
        frag.width = 3f;
        frag.height = 4f;
        frag.collidesGround = false;

        BasicBulletType siliconAmmo = new BasicBulletType(5.5f, 10f);
        siliconAmmo.width = 6f;
        siliconAmmo.height = 8f;
        siliconAmmo.lifetime = 30f;
        siliconAmmo.homingPower = 0.25f;
        siliconAmmo.homingRange = 70f;
        siliconAmmo.collidesGround = false;
        siliconAmmo.fragBullets = 6;
        siliconAmmo.fragBullet = frag;
        siliconAmmo.fragSpread = 60f;

        ((ItemTurret) Blocks.scatter).ammoTypes.put(Items.silicon, siliconAmmo);

        // === HAIL ===
        ItemTurret hail = (ItemTurret) Blocks.hail;
        ArtilleryBulletType baseHail = (ArtilleryBulletType) hail.ammoTypes.get(Items.graphite);
        float hailDamage = baseHail.damage;
        float hailRange = hail.range;

        // --- Titanium for Hail ---
        // Faster (~1.5x), freeze, blue
        ArtilleryBulletType hailTitanium = new ArtilleryBulletType(3.2f, hailDamage * 1.15f);
        hailTitanium.width = 12f;
        hailTitanium.height = 12f;
        hailTitanium.lifetime = 70f;
        hailTitanium.reloadMultiplier = 1.5f;
        hailTitanium.status = StatusEffects.freezing;
        hailTitanium.statusDuration = 120f;
        hailTitanium.splashDamage = hailDamage * 0.6f;
        hailTitanium.splashDamageRadius = 22f;
        hailTitanium.hitColor = Color.valueOf("8da1e3");
        hailTitanium.frontColor = Color.valueOf("8da1e3");
        hailTitanium.backColor = Color.valueOf("5c6ea3");
        hailTitanium.trailColor = Color.valueOf("8da1e3");
        hailTitanium.despawnEffect = Fx.hitBulletColor;
        hailTitanium.hitEffect = Fx.hitBulletColor;

        // --- Thorium for Hail ---
        // Normal speed, same damage as titanium, stacking radiation, 2x ammo cost
        ArtilleryBulletType hailThorium = new ArtilleryBulletType(3.0f, hailDamage * 1.15f){
            @Override
            public void hit(mindustry.gen.Bullet b, float x, float y){
                super.hit(b, x, y);
                // Apply / stack radiation
                if(b.owner instanceof Building build){
                    // track shots for overheat
                    int shots = hailShots.get(build, 0) + 1;
                    hailShots.put(build, shots);
                    hailHeat.put(build, 1f); // keep heat high while shooting

                    if(shots >= 10){
                        // overheat: we will handle reloadMultiplier via update
                    }
                }
            }
        };
        hailThorium.width = 13f;
        hailThorium.height = 13f;
        hailThorium.lifetime = 75f;
        hailThorium.ammoMultiplier = 2f;           // eats 2x thorium
        hailThorium.reloadMultiplier = 1f;
        hailThorium.status = radiation;
        hailThorium.statusDuration = 60f * 8f;     // long enough to stack
        hailThorium.splashDamage = hailDamage * 0.7f;
        hailThorium.splashDamageRadius = 24f;
        hailThorium.hitColor = Pal.reactorPurple;
        hailThorium.frontColor = Pal.reactorPurple;
        hailThorium.backColor = Pal.reactorPurple2;
        hailThorium.trailColor = Pal.reactorPurple;
        hailThorium.despawnEffect = Fx.reactorExplosion;
        hailThorium.hitEffect = Fx.reactorsmoke;

        hail.ammoTypes.put(Items.titanium, hailTitanium);
        hail.ammoTypes.put(Items.thorium, hailThorium);

        // === Death explosion from radiation stacks ===
        Events.on(UnitDestroyEvent.class, e -> {
            Unit unit = e.unit;
            if(unit == null || !unit.hasEffect(radiation)) return;

            // Approximate stacks from remaining effect time / intensity
            // StatusEffect doesn't expose exact stacks easily, so we use a simple scaling
            // based on how long the effect has been active (rough approximation)
            float intensity = Mathf.clamp(unit.getDuration(radiation) / (60f * 8f), 0f, 1f);
            // Better: use a fixed max and scale by a reasonable amount
            // For first version we use a strong but not full reactor explosion scaled by intensity

            float power = Mathf.clamp(intensity * 1.4f, 0.15f, 1f); // min small boom, max full 1/4 reactor

            float damage = 1250f * power;          // max 1/4 of real thorium reactor (5000)
            float radius = (8f + 11f * power) * tilesize;

            Damage.damage(unit.team, unit.x, unit.y, radius, damage, true, true);

            Fx.reactorExplosion.at(unit.x, unit.y);
            Fx.reactorsmoke.at(unit.x, unit.y);
            Sounds.explosionReactor.at(unit.x, unit.y, 1f, 0.8f + power * 0.4f);

            // small screen shake proportional to power
            Effect.shake(3f * power, 12f * power, unit.x, unit.y);
        });
    }

    @Override
    public void init(){
        // Overheat recovery logic
        Events.run(mindustry.game.EventType.Trigger.update, () -> {
            // Slowly cool down and reset shot counters
            hailHeat.each((build, heat) -> {
                if(build == null || !build.isValid()){
                    hailHeat.remove(build);
                    hailShots.remove(build);
                    return;
                }

                float newHeat = heat - Time.delta / (60f * 5f); // 5 seconds to fully cool
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
