package sonar.calculator.mod.common.tileentity.misc;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.api.IFluxPoint;
import sonar.calculator.mod.client.gui.misc.GuiFlux;
import sonar.calculator.mod.common.item.modules.LocatorModule;
import sonar.calculator.mod.network.ChunkHandler;
import sonar.calculator.mod.utils.FluxNetwork;
import sonar.calculator.mod.utils.FluxRegistry;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.utils.IMachineButtons;
import sonar.core.utils.ISyncTile;
import sonar.core.utils.SonarAPI;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFluxController extends TileEntityInventory implements IFluxPoint, ISyncTile, IMachineButtons {

	private Ticket currentTicket;

	public int recieveMode, sendMode;
	public int transmitterMode, playerProtect;
	public int dimension, networkID;
	public String playerName, networkName = "NETWORK";

	/** client only list */
	public List<FluxNetwork> networks;

	public TileEntityFluxController() {
		super.slots = new ItemStack[9];
	}

	public int pushEnergy(int recieve, boolean simulate) {
		List<String> players = getPlayers();
		if (players != null && players.size() > 0) {
			for (int p = 0; p < players.size(); p++) {

				MinecraftServer server = MinecraftServer.getServer();
				World world = server.worldServerForDimension(dimension);
				if (world.getPlayerEntityByName(players.get(p)) != null) {
					EntityPlayer player = world.getPlayerEntityByName(players.get(p));

					if (player != null && player.inventory != null) {
						IInventory inv = player.inventory;
						for (int i = 0; i < inv.getSizeInventory(); i++) {
							ItemStack target = inv.getStackInSlot(i);
							if (target != null) {
								if (target.getItem() instanceof IEnergyContainerItem) {
									if (((IEnergyContainerItem) target.getItem()).getEnergyStored(target) != ((IEnergyContainerItem) target.getItem()).getMaxEnergyStored(target)) {
										recieve -= ((IEnergyContainerItem) target.getItem()).receiveEnergy(inv.getStackInSlot(i), recieve, simulate);
									}
								} else if (SonarAPI.ic2Loaded() && target.getItem() instanceof IElectricItem) {
									IElectricItem item = (IElectricItem) target.getItem();
									IElectricItemManager manager = ElectricItem.manager;

									int itemEnergyRF = (int) Math.round(Math.min(Math.sqrt(item.getMaxCharge(target) * 4), (item.getMaxCharge(target) * 4) - (manager.getCharge(target) * 4)));
									int toTransferRF = Math.min(recieve, Math.round(itemEnergyRF));

									recieve -= manager.charge(target, toTransferRF / 4, 4, false, simulate) * 4;
								} else if (SonarAPI.ic2Loaded() && target.getItem() instanceof ISpecialElectricItem) {

									ISpecialElectricItem item = (ISpecialElectricItem) target.getItem();
									IElectricItemManager manager = item.getManager(target);

									int itemEnergyRF = (int) Math.round(Math.min(Math.sqrt(item.getMaxCharge(target) * 4), (item.getMaxCharge(target) * 4) - (manager.getCharge(target) * 4)));
									int toTransferRF = Math.min(recieve, Math.round(itemEnergyRF));

									recieve -= manager.charge(target, toTransferRF / 4, 4, false, simulate) * 4;
								}
							}
						}
					}
				}
			}
		}
		return recieve;
	}

	public void addToFrequency() {
		if (!this.worldObj.isRemote) {
			FluxRegistry.addController(this);
		}
	}

	public void removeFromFrequency() {
		if (!this.worldObj.isRemote) {
			FluxRegistry.removeController(this);
		}
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {

			this.recieveMode = nbt.getInteger("recieveMode");
			this.sendMode = nbt.getInteger("sendMode");
			this.networkID = nbt.getInteger("networkID");
			this.networkName = nbt.getString("networkName");
			this.transmitterMode = nbt.getInteger("transmitterMode");
			this.playerProtect = nbt.getInteger("playerProtect");
			if (type == SyncType.SAVE) {
				this.dimension = nbt.getInteger("DIMENSION");
				this.playerName = nbt.getString("playerName");
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {

			nbt.setInteger("recieveMode", recieveMode);
			nbt.setInteger("sendMode", sendMode);
			nbt.setInteger("networkID", networkID);
			nbt.setString("networkName", networkName);
			nbt.setInteger("transmitterMode", transmitterMode);
			nbt.setInteger("playerProtect", playerProtect);

			if (type == SyncType.SAVE) {
				nbt.setInteger("DIMENSION", dimension);
				nbt.setString("playerName", playerName);
			}

		}
	}

	public void onLoaded() {
		if (!this.worldObj.isRemote) {
			int network = FluxRegistry.getNetwork(networkName, playerName);
			if (network == 0) {
				this.networkID = FluxRegistry.createNetwork(networkName);
			} else {
				this.networkID = network;
			}
			this.addToFrequency();
			loadChunks();
		}
	}

	/** keeps the Flux Controller loaded */
	public void loadChunks() {
		this.currentTicket = ForgeChunkManager.requestTicket(Calculator.instance, this.worldObj, Type.NORMAL);
		if (currentTicket != null) {
			ChunkCoordIntPair chunk = new ChunkCoordIntPair(xCoord >> 4, zCoord >> 4);
			ChunkHandler.saveFluxController(currentTicket, this);
			ForgeChunkManager.forceChunk(currentTicket, chunk);
		}
	}

	/** stops the Flux Controller from being loaded */
	public void removeChunks() {
		if (currentTicket != null) {
			ForgeChunkManager.releaseTicket(currentTicket);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (!this.worldObj.isRemote) {
			this.removeFromFrequency();
		}
	}

	public List<String> getPlayers() {
		List<String> players = new ArrayList();
		if (playerName != null) {
			if (playerName != null) {
				players.add(playerName);
			}
			for (int i = 0; i < 9; i++) {
				if (slots[i] != null) {
					if (slots[i].getItem() == Calculator.itemLocatorModule) {
						String locatorPlayer = LocatorModule.getPlayer(slots[i]);
						if (locatorPlayer != null) {
							if (!players.contains(locatorPlayer)) {
								players.add(locatorPlayer);
							}
						}
					}
				}
			}
		}
		return players;
	}

	public boolean validPlayer(String playerName) {
		if (playerName == null)
			return true;

		switch (this.playerProtect) {
		case 0:
			return getPlayers().contains(playerName);
		case 1:
			return playerName.equals(this.playerName);
		case 2:
			return true;
		}

		return false;
	}

	public String getPlayer() {
		return this.playerName;
	}

	public void setName(String name) {
		if (!name.isEmpty() && !name.equals("NETWORK")) {
			this.removeFromFrequency();
			int network = FluxRegistry.getNetwork(name, playerName);
			if (network == 0) {
				this.networkID = FluxRegistry.createNetwork(name);
			} else {
				if (FluxRegistry.getController(network) == null) {
					this.networkID = network;
				}
			}
			if (FluxRegistry.getController(network) == null) {
				this.addToFrequency();
				networkName = name;
			}
		}
	}

	public void rename(String name) {
		if (!name.isEmpty() && !name.equals("NETWORK")) {
			FluxRegistry.renameNetwork(playerName, networkName, name);
		}
	}

	public void setPlayer(EntityPlayer player) {
		if (player == null) {
			return;
		}
		this.playerName = player.getGameProfile().getName();
		this.dimension = player.dimension;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (stack == null) {
			return false;
		}
		return stack.getItem() == Calculator.itemLocatorModule;
	}

	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public int xCoord() {
		return this.xCoord;
	}

	@Override
	public int yCoord() {
		return this.yCoord;
	}

	@Override
	public int zCoord() {
		return this.zCoord;
	}

	@Override
	public int networkID() {
		return this.networkID;
	}

	@Override
	public int dimension() {
		return this.dimension;
	}

	@Override
	public int maxTransfer() {
		return 64000;
	}

	@Override
	public int priority() {
		return 0;
	}

	@Override
	public String masterName() {
		return this.playerName;
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		if (networkName.equals("NETWORK")) {
			currenttip.add(FontHelper.translate("network.notConnected"));
		} else {
			currenttip.add(networkName + ": " + GuiFlux.getNetworkType(this.playerProtect));
		}
		return currenttip;
	}

	@Override
	public void buttonPress(int buttonID, int value) {
		switch (buttonID) {
		case 3:
			if (recieveMode < 4)
				recieveMode++;
			else
				recieveMode = 0;
			break;
		case 4:
			if (sendMode < 2)
				sendMode++;
			else
				sendMode = 0;
			break;
		case 5:
			if (transmitterMode == 0)
				transmitterMode = 1;
			else
				transmitterMode = 0;
			break;
		case 6:
			if (playerProtect < 2)
				playerProtect++;
			else
				playerProtect = 0;
			break;
		}

	}
}
