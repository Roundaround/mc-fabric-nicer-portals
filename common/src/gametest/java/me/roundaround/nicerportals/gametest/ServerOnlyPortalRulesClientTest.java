package me.roundaround.nicerportals.gametest;

import me.roundaround.allay.api.gametest.ClientGameTest;
import me.roundaround.trove.gametest.ClientTest;
import me.roundaround.trove.gametest.ClientTestContext;
import me.roundaround.trove.gametest.ClientWorld;
import me.roundaround.trove.gametest.GameTestAssertionException;
import net.minecraft.core.Direction;
import net.minecraft.world.level.portal.PortalShape;

/**
 * Regression guard for GH-16 (crash when breaking or lighting a portal on a multiplayer client).
 *
 * <p>{@link me.roundaround.nicerportals.config.NicerPortalsPerWorldConfig} is world-scoped: its options
 * only exist once a world directory is attached, which only happens where a server loads a save. A client
 * connected to a remote server never attaches one, so every mod hook that reads it has to stay off the
 * logical client — otherwise the first portal the player breaks or lights NPEs. The client's only uses
 * for the scan were a result vanilla discards ({@code Block#updateOrDestroy} guards the destroy on
 * {@code !isClientSide}) and block prediction that {@code BlockStatePredictionHandler} rolls back to the
 * server's state on ack, so dropping out of both costs nothing but a round trip of visual latency.
 *
 * <p>Builds a crying-obsidian frame — a frame vanilla rejects outright and only the mod's
 * {@code portalFrameTag} rule accepts — and lights it. The portal forming proves the server applied the
 * mod's rules; running the same scan against the {@code ClientLevel} then has to come back invalid,
 * which is only true if the hooks really did bail out client-side. Single-player, creative.
 */
@ClientGameTest
public class ServerOnlyPortalRulesClientTest implements ClientTest {
  @Override
  public void runTest(ClientTestContext context) {
    try (ClientWorld world = context.worldBuilder().creative().stopTime(true).create()) {
      PortalTests.platform(world);
      PortalTests.standardFrame(world, "minecraft:crying_obsidian");
      world.teleport(1.5, 64.0, -1.0);
      context.waitTicks(2);

      // The server accepts the crying-obsidian frame, so the portal lights.
      PortalTests.igniteWithFlintAndSteel(context, world, PortalTests.INTERIOR_BOTTOM);

      // The client must not. Vanilla's FRAME predicate is obsidian-only, so a scan of the same frame
      // on the ClientLevel finds no bottom-left at all and reports width/height 0.
      context.runOnClient((mc) -> {
        PortalShape shape = PortalShape.findAnyShape(mc.level, PortalTests.INTERIOR_BOTTOM, Direction.Axis.X);
        if (shape.isValid()) {
          throw new GameTestAssertionException(
              "the PortalShape scan applied the mod's frame rules on the ClientLevel; the logical client "
              + "has to stay on vanilla rules so it never reads the per-world config it cannot load (GH-16)");
        }
      });
    }
  }
}
