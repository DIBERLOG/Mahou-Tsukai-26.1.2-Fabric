package stepsword.mahoutsukai.fabric.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stepsword.mahoutsukai.dataattachments.livingmahou.LivingMahou;
import stepsword.mahoutsukai.dataattachments.livingmahou.LivingMahouStorage;
import stepsword.mahoutsukai.fabric.LivingMahouHolder;

@Mixin(LivingEntity.class)
public abstract class LivingMahouMixin implements LivingMahouHolder {
    @Unique private final LivingMahou mahoutsukai$living = new LivingMahou();

    @Inject(method = "tick", at = @At("HEAD"))
    private void mahoutsukai$probabilityTick(CallbackInfo ci) {
        stepsword.mahoutsukai.potion.FearPotion.fearLivingUpdate((LivingEntity)(Object)this);
        if(!((LivingEntity)(Object)this).level().isClientSide()) stepsword.mahoutsukai.potion.ConfusionPotion.confusionLivingUpdate((LivingEntity)(Object)this);
        if(!((LivingEntity)(Object)this).level().isClientSide()) stepsword.mahoutsukai.effects.familiar.FamiliarsGardenEffect.familiarsGardenLivingUpdate((LivingEntity)(Object)this);
        stepsword.mahoutsukai.potion.BlackFlameEyesPotion.blackFlameEyesLivingUpdate((LivingEntity)(Object)this);
        stepsword.mahoutsukai.potion.ReversionEyesPotion.reversionEyesLivingUpdate((LivingEntity)(Object)this);
        if(!((LivingEntity)(Object)this).level().isClientSide()){stepsword.mahoutsukai.item.spells.secret.ripper.TheRipper.ripperLivingTick((LivingEntity)(Object)this);stepsword.mahoutsukai.potion.RipperInvisPotion.presenceConcealmentLivingTick((LivingEntity)(Object)this);}
        stepsword.mahoutsukai.potion.BorrowedAuthorityPotion.authorityLivingUpdate((LivingEntity)(Object)this);stepsword.mahoutsukai.potion.AuthorityJumpPotion.authorityJumpLivingTick((LivingEntity)(Object)this);stepsword.mahoutsukai.potion.WallStickPotion.wallStickLivingTick((LivingEntity)(Object)this);
        stepsword.mahoutsukai.potion.MisfortunePotion.misfortuneLivingUpdate((LivingEntity)(Object)this);
        stepsword.mahoutsukai.potion.FlungPotion.flungLivingTick((LivingEntity)(Object)this);
        stepsword.mahoutsukai.potion.WoundedPotion.woundedLivingTick((LivingEntity)(Object)this);
        stepsword.mahoutsukai.potion.ProbabilityAlterPotion.applyProbabilityAlter((LivingEntity)(Object)this);
    }
    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void mahoutsukai$ruleBreakerHit(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source,
            float amount, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        stepsword.mahoutsukai.item.rulebreaker.RuleBreaker.ruleBreakerLivingHurt((LivingEntity)(Object)this, source);
    }
    @Override
    public LivingMahou mahoutsukai$getLivingMahou() { return mahoutsukai$living; }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void mahoutsukai$read(ValueInput input, CallbackInfo ci) {
        stepsword.mahoutsukai.fabric.LegacyMahouData.attachment(input,"living_mahou")
            .ifPresent(child -> LivingMahouStorage.read(mahoutsukai$living, child));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void mahoutsukai$write(ValueOutput output, CallbackInfo ci) {
        LivingMahouStorage.write(mahoutsukai$living, output.child("mahoutsukai:living_mahou"));
    }
}
