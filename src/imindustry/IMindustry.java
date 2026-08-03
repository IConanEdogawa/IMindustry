package imindustry;

import arc.graphics.Color;
import mindustry.content.*;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.mod.Mod;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class IMindustry extends Mod {

    @Override
    public void loadContent() {
        // === Duo Enhanced Ammo ===
        ItemTurret duo = (ItemTurret) Blocks.duo;
        BasicBulletType siliconBullet = (BasicBulletType) duo.ammoTypes.get(Items.silicon);
        float baseRange = duo.range;

        // Titanium: 2x damage, half fire rate, Slow, slightly longer range
        BasicBulletType titanBullet = new BasicBulletType(4.5f, siliconBullet.damage * 2f);
        titanBullet.width = 7f;
        titanBullet.height = 10f;
        titanBullet.lifetime = 40f;
        titanBullet.reloadMultiplier = 0.5f;
        titanBullet.status = StatusEffects.slow;
        titanBullet.statusDuration = 90f;
        titanBullet.rangeOverride = baseRange + 16f;
        titanBullet.hitColor = Color.valueOf("8da1e3");
        titanBullet.trailColor = Color.valueOf("8da1e3");
        titanBullet.frontColor = Color.valueOf("8da1e3");
        titanBullet.backColor = Color.valueOf("5c6ea3");

        // Sand: cheap, lower damage, strong knockback
        BasicBulletType sandBullet = new BasicBulletType(4f, siliconBullet.damage * 0.6f);
        sandBullet.lifetime = 35f;
        sandBullet.knockback = 1.4f;
        sandBullet.rangeOverride = baseRange + 16f;
        sandBullet.hitColor = Color.valueOf("f7cba4");
        sandBullet.trailColor = Color.valueOf("f7cba4");
        sandBullet.frontColor = Color.valueOf("f7cba4");
        sandBullet.backColor = Color.valueOf("c9986f");

        duo.acceptsItems = true;
        duo.ammoTypes.put(Items.titanium, titanBullet);
        duo.ammoTypes.put(Items.sand, sandBullet);

        // === Scatter: Silicon ammo ===
        BasicBulletType shrapnel = new BasicBulletType(3.5f, 6f);
        shrapnel.lifetime = 18f;
        shrapnel.width = 3f;
        shrapnel.height = 4f;
        shrapnel.hitColor = Color.valueOf("53565c");
        shrapnel.trailColor = Color.valueOf("53565c");
        shrapnel.frontColor = Color.valueOf("53565c");
        shrapnel.backColor = Color.valueOf("2f3134");
        shrapnel.collidesGround = false;

        BasicBulletType siliconScatterBullet = new BasicBulletType(5.5f, 10f);
        siliconScatterBullet.width = 6f;
        siliconScatterBullet.height = 8f;
        siliconScatterBullet.lifetime = 30f;
        siliconScatterBullet.homingPower = 0.25f;
        siliconScatterBullet.homingRange = 70f;
        siliconScatterBullet.collidesGround = false;
        siliconScatterBullet.fragBullets = 6;
        siliconScatterBullet.fragBullet = shrapnel;
        siliconScatterBullet.fragSpread = 60f;
        siliconScatterBullet.fragVelocityMin = 0.5f;
        siliconScatterBullet.fragVelocityMax = 1.1f;
        siliconScatterBullet.hitColor = Color.valueOf("53565c");
        siliconScatterBullet.trailColor = Color.valueOf("53565c");
        siliconScatterBullet.frontColor = Color.valueOf("53565c");
        siliconScatterBullet.backColor = Color.valueOf("2f3134");

        ItemTurret scatter = (ItemTurret) Blocks.scatter;
        scatter.ammoTypes.put(Items.silicon, siliconScatterBullet);
    }
}
