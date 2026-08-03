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

    public static StatusEffect radiation;

    private static final ObjectMap<Building, Float> hailHeat = new ObjectMap<>();
    private static final ObjectMap<Building, Integer> hailShots = new ObjectMap<>();

    @Override
    public void loadContent() {

        // Radiation status - light DoT, mainly for stacking + death trigger
        radiation = new StatusEffect("im-radiation"){{
            color = Pal.reactorPurple;
            damage = 0.04f;              // very light damage over time
            transitionDamage = 2f;
            effect = Fx.reactorsmoke;
            effectChance = 0.04f;        // rare small puffs
        }};

        // === Duo ===
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

        // === Scatter ===
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

        // Titanium - faster + freeze + blue
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

        // Thorium - radiation stacks, NO big explosion on hit
        ArtilleryBulletType hailThorium = new ArtilleryBulletType(3.0f, hailDamage * 1.15f){
            @Override
            public void hit(mindustry.gen.Bullet b, float x, float y){
                super.hit(b, x, y);
                if(b.owner instanceof Building build){
                    int shots = hailShots.get(build, 0) + 1;
                    hailShots.put(build, shots);
                    hailHeat.put(build, 1f);
                }
            }
        };
        hailThorium.width = 13f;
        hailThorium.height = 13f;
        hailThorium.lifetime = 75f;
        hailThorium.ammoMultiplier = 2f;
        hailThorium.reloadMultiplier = 1f;
        hailThorium.status = radiation;
        hailThorium.statusDuration = 60f * 6f;   // 6 seconds base duration
        hailThorium.splashDamage = hailDamage * 0.55f;
        hailThorium.splashDamageRadius = 20f;
        hailThorium.hitColor = Pal.reactorPurple;
        hailThorium.frontColor = Pal.reactorPurple;
        hailThorium.backColor = Pal.reactorPurple2;
        hailThorium.trailColor = Pal.reactorPurple;

        // Small purple hit only - NO reactor explosion on hit
        hailThorium.hitEffect = Fx.hitBulletColor;
        hailThorium.despawnEffect = Fx.hitBulletColor;

        hail.ammoTypes.put(Items.titanium, hailTitanium);
        hail.ammoTypes.put(Items.thorium, hailThorium);

        // === Death explosion (only on death, small fog) ===
        Events.on(UnitDestroyEvent.class, e -> {
            Unit unit = e.unit;
            if(unit == null || !unit.hasEffect(radiation)) return;

            // Scale by remaining duration (rough stacks approximation)
            float intensity = Mathf.clamp(unit.getDuration(radiation) / (60f * 6f), 0.1f, 1f);

            // Max damage = 1/4 of real reactor (1250), min much smaller
            float damage = 180f + 1070f * intensity;   // 180 ~ 1250
            float radius = (2.2f + 4.5f * intensity) * tilesize; // ~2 to ~7 blocks

            Damage.damage(unit.team, unit.x, unit.y, radius, damage, true, true);

            // Small purple explosion + tiny smoke (about 2 blocks feel)
            Fx.reactorExplosion.at(unit.x, unit.y, intensity * 0.35f); // scaled down heavily
            Fx.reactorsmoke.at(unit.x, unit.y, 0.4f);

            Sounds.explosionReactor.at(unit.x, unit.y, 1.1f, 0.35f + intensity * 0.35f);

            Effect.shake(1.2f * intensity, 6f * intensity, unit.x, unit.y);
        });
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
