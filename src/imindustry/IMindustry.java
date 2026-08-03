package imindustry;

import arc.graphics.Color;
import mindustry.content.*;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.mod.Mod;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class IMindustry extends Mod {

    @Override
    public void loadContent() {
        ItemTurret duo = (ItemTurret) Blocks.duo;
        BasicBulletType silicon = (BasicBulletType) duo.ammoTypes.get(Items.silicon);
        float range = duo.range;

        BasicBulletType titaniumAmmo = new BasicBulletType(4.5f, silicon.damage * 2f);
        titaniumAmmo.width = 7f;
        titaniumAmmo.height = 10f;
        titaniumAmmo.lifetime = 40f;
        titaniumAmmo.reloadMultiplier = 0.5f;
        titaniumAmmo.status = StatusEffects.slow;
        titaniumAmmo.statusDuration = 90f;
        titaniumAmmo.rangeOverride = range + 16f;
        titaniumAmmo.hitColor = Color.valueOf("8da1e3");
        titaniumAmmo.frontColor = Color.valueOf("8da1e3");
        titaniumAmmo.backColor = Color.valueOf("5c6ea3");

        BasicBulletType sandAmmo = new BasicBulletType(4f, silicon.damage * 0.6f);
        sandAmmo.lifetime = 35f;
        sandAmmo.knockback = 1.4f;
        sandAmmo.rangeOverride = range + 16f;
        sandAmmo.hitColor = Color.valueOf("f7cba4");
        sandAmmo.frontColor = Color.valueOf("f7cba4");
        sandAmmo.backColor = Color.valueOf("c9986f");

        duo.ammoTypes.put(Items.titanium, titaniumAmmo);
        duo.ammoTypes.put(Items.sand, sandAmmo);

        // Scatter Silicon
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
    }
}
