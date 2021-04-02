package io.craigcarr.main;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntitySummonArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class Commands implements ModInitializer {
	
    public static final String MOD_ID = "commands";
    public static final String MOD_NAME = "Commands";
    public static final String MOD_VER = "1.0.0";

    public static Logger LOGGER = LogManager.getLogger();

    public static void log(Level level, String message){
        if(Config.INSTANCE.log == null || level.isMoreSpecificThan(Config.INSTANCE.log))
            LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }
    
	@Override
	public void onInitialize() {
		
        log(Level.INFO, "version " + MOD_VER);
        Config.loadConfig();
        log(Level.INFO, "Initialized successfully.");
        
		//enderchest
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("ec").executes(context -> {
            	
            	ServerPlayerEntity player = context.getSource().getPlayer();
            	
            	EnderChestInventory eci = player.getEnderChestInventory();
            	
            	
            	player.openHandledScreen(
            	new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
        			return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory , eci);
        		}, new TranslatableText("container.enderchest")));
       
            	player.incrementStat(Stats.OPEN_ENDERCHEST);
    
                return 1;
            }));
        });
		
		//long enderchest
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("enderchest").executes(context -> {
            	
            	ServerPlayerEntity player = context.getSource().getPlayer();
            	
            	EnderChestInventory eci = player.getEnderChestInventory();
            	
            	
            	player.openHandledScreen(
            	new SimpleNamedScreenHandlerFactory((i, playerInventory, playerEntity) -> {
        			return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory , eci);
        		}, new TranslatableText("container.enderchest")));
       
            	player.incrementStat(Stats.OPEN_ENDERCHEST);
    
                return 1;
            }));
        });
		
		//workbench
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			
			dispatcher.register(literal("wb").executes(context -> {
            	
            	ServerPlayerEntity player = context.getSource().getPlayer();
      
            	
            	player.openHandledScreen(
            	new SimpleNamedScreenHandlerFactory((syncId, playerInventory, somevalue) -> {
            		
            		CraftingScreenHandler csh = new CraftingScreenHandler(syncId, playerInventory, ScreenHandlerContext.EMPTY);
            		
        			return  csh;
        			
        		}, new TranslatableText("container.crafting")));
       
            	player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            	
                return 1;
                
            }));
        });
		
		//long workbench
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			
			dispatcher.register(literal("workbench").executes(context -> {
            	
				ServerPlayerEntity player = context.getSource().getPlayer();
      
            	
            	player.openHandledScreen(
            	new SimpleNamedScreenHandlerFactory((syncId, playerInventory, somevalue) -> {
            		
            		CraftingScreenHandler csh = new CraftingScreenHandler(syncId, playerInventory, ScreenHandlerContext.EMPTY);
            		
        			return  csh;
        			
        		}, new TranslatableText("container.crafting")));
       
            	player.incrementStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
            	
                return 1;
                
            }));
        });
		
		//trash
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			
			dispatcher.register(literal("trash").executes(context -> {
            	
            	ServerPlayerEntity player = context.getSource().getPlayer();
            	            	
            	player.openHandledScreen(
            	new SimpleNamedScreenHandlerFactory((i, playerInventory, blah) -> {
            		return GenericContainerScreenHandler.createGeneric9x3(i, playerInventory, new SimpleInventory(27)); 
        		}, new LiteralText("Trash Can")));

    
                return 1;
            }));
        });
		
       
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
        	 LiteralArgumentBuilder<ServerCommandSource> command = literal("perimeterinfo").
                     executes( (c) -> perimeterDiagnose(
                             c.getSource(),
                             new BlockPos(c.getSource().getPosition()),
                             null)).
                     then(argument("center position", BlockPosArgumentType.blockPos()).
                             executes( (c) -> perimeterDiagnose(
                                     c.getSource(),
                                     BlockPosArgumentType.getBlockPos(c, "center position"),
                                     null)).
                             then(argument("mob",EntitySummonArgumentType.entitySummon()).
                                     suggests(SuggestionProviders.SUMMONABLE_ENTITIES).
                                     executes( (c) -> perimeterDiagnose(
                                             c.getSource(),
                                             BlockPosArgumentType.getBlockPos(c, "center position"),
                                             EntitySummonArgumentType.getEntitySummon(c, "mob").toString()
                                     ))));
        	dispatcher.register(command);
        });
     
	   
	}
	
	 private static int perimeterDiagnose(ServerCommandSource source, BlockPos pos, String mobId)
	    {
		 	Commands.log(Level.ALL, "Entered Perimeter Diagnose");
	        CompoundTag nbttagcompound = new CompoundTag();
	        MobEntity entityliving = null;
	        if (mobId != null)
	        {
	            nbttagcompound.putString("id", mobId);
	            Entity baseEntity = EntityType.loadEntityWithPassengers(nbttagcompound, source.getWorld(), (entity_1x) -> {
	                entity_1x.refreshPositionAndAngles(new BlockPos(pos.getX(), -10, pos.getZ()), entity_1x.yaw, entity_1x.pitch);
	                return !source.getWorld().tryLoadEntity(entity_1x) ? null : entity_1x;
	            });
	            if (!(baseEntity instanceof  MobEntity))
	            {
	                System.out.println("r Failed to spawn test entity");
	                if (baseEntity != null) baseEntity.remove();
	                return 0;
	            }
	            entityliving = (MobEntity) baseEntity;
	        }
	        PerimeterDiagnostics.Result res = PerimeterDiagnostics.countSpots(source.getWorld(), pos, entityliving);

	        if (entityliving != null)
	        {
	        	BlockPos bpos = new BlockPos(0, 80, 0);
	        	
	        	source.getWorld().setBlockState(bpos, Blocks.STONE_SLAB.getDefaultState());
	        	
	        	
	            res.samples.forEach(bp -> {source.getWorld().setBlockState(bp, Blocks.STONE_SLAB.getDefaultState());});
	            
	            entityliving.remove();
	        }
	        return 1;
	    }
}
