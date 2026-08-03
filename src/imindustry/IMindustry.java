package imindustry;

import arc.graphics.Color;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.mod.Mod;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class IMindustry extends Mod {

    @Override
    public void loadContent() {
        // === Duo Enhanced Ammo ===
        ItemTurret duo = (ItemTurret) Blocks.duo;
        BasicBulletType siliconBullet = (BasicBulletType) duo.ammoTypes.get(Items.silicon);
        float baseRange = duo.range;

        // Titanium: heavy, slow, mid-game "second wind" shot
        // Half fire rate, double damage of silicon, applies Slow
        BasicBulletType titanBullet = new BasicBulletType(4.5f, siliconBullet.damage * 2f) {{
            width = 7f;
            height = 10f;
            lifetime = 40f;
            reloadMultiplier = 0.5f;
            status = StatusEffects.slow;
            statusDuration = 90f;
            rangeOverride = baseRange + 16f;
            hitColor = Color.valueOf("8da1e3");
            trailColor = Color.valueOf("8da1e3");
            frontColor = Color.valueOf("8da1e3");
            backColor = Color.valueOf("5c6ea3");
        }};

        // Sand: cheap, average damage, strong knockback for crowd control
        BasicBulletType sandBullet = new BasicBulletType(4f, siliconBullet.damage * 0.6f) {{
            lifetime = 35f;
            knockback = 1.4f;
            rangeOverride = baseRange + 16f;
            hitColor = Color.valueOf("f7cba4");
            trailColor = Color.valueOf("f7cba4");
            frontColor = Color.valueOf("f7cba4");
            backColor = Color.valueOf("c9986f");
        }};

        duo.acceptsItems = true;
        duo.ammoTypes.put(Items.titanium, titanBullet);
        duo.ammoTypes.put(Items.sand, sandBullet);

        // === Scatter: Silicon ammo ===
        // Homing flak that bursts into shrapnel on impact
        BasicBulletType shrapnel = new BasicBulletType(3.5f, 6f) {{
            lifetime = 18f;
            width = 3f;
            height = 4f;
            hitColor = Color.valueOf("53565c");
            trailColor = Color.valueOf("53565c");
            frontColor = Color.valueOf("53565c");
            backColor = Color.valueOf("2f3134");
            collidesGround = false;
        }};

        BasicBulletType siliconScatterBullet = new BasicBulletType(5.5f, 10f) {{
            width = 6f;
            height = 8f;
            lifetime = 30f;
            homingPower = 0.25f;
            homingRange = 70f;
            collidesGround = false;
            fragBullets = 6;
            fragBullet = shrapnel;
            fragSpread = 60f;
            fragVelocityMin = 0.5f;
            fragVelocityMax = 1.1f;
            hitColor = Color.valueOf("53565c");
            trailColor = Color.valueOf("53565c");
            frontColor = Color.valueOf("53565c");
            backColor = Color.valueOf("2f3134");
        }};

        ItemTurret scatter = (ItemTurret) Blocks.scatter;
        scatter.ammoTypes.put(Items.silicon, siliconScatterBullet);
    }
}
