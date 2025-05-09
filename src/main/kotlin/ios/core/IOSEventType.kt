package ios.core

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import core.moshi.moshi
import kotlinx.serialization.Serializable

@Serializable
data class IOSEventType(
    val uuid: String,
    val x: Int,
    val y: Int,
) {
    companion object {
        val recordedEventAdapter: JsonAdapter<List<IOSEventType>>
            get() {
                val type = Types.newParameterizedType(List::class.java, IOSEventType::class.java)
                return moshi.adapter(type)
            }
    }
}

/**
 * import ATSupport
 * import os.log
 * import UIKit
 *
 * final class ATWindow: UIWindow {
 *     private let logger = os.Logger(
 *         subsystem: Bundle.main.bundleIdentifier ?? "com.salamr",
 *         category: "UserInputs")
 *
 *     override public func motionEnded(
 *         _ motion: UIEvent.EventSubtype, with _: UIEvent?
 *     ) {
 *         if motion == .motionShake,
 *             isDebug || AppConfig.shared.featureManager.isDeveloperModeEnabled
 *         {
 *             let topMostController =
 *                 rootViewController?.presentedViewController
 *                 ?? rootViewController
 *
 *             topMostController?.present(
 *                 BaseNavigationController(
 *                     rootViewController: HiddenMenuViewController()),
 *                 animated: true
 *             )
 *
 *         }
 *     }
 *
 *     override public func sendEvent(_ event: UIEvent) {
 *         // Get all touches from the event
 *         let touches = event.allTouches
 *
 *         // Iterate over each touch
 *         for touch in touches ?? [] {
 *             let phase = touch.phase
 *             let location = touch.location(in: touch.window)
 *             let timestamp = event.timestamp
 *
 *             // Log Tap events (when phase is Began or Ended)
 *             if phase == .began || phase == .ended {
 *                 logger.info(
 *                     "salamr-- Tap Event - Timestamp: \(timestamp), Location: \(location.x), \(location.y), Phase: \(phase.rawValue)"
 *                 )
 *             }
 *
 *             // Log Swipe events (when phase is Moved)
 *             if phase == .moved {
 *                 logger.info(
 *                     "Swipe Event - Timestamp: \(timestamp), Location: \(location.x), \(location.y), Phase: \(phase.rawValue)"
 *                 )
 *             }
 *         }
 *
 *         // Call the super implementation to ensure the event is passed to the system
 *         super.sendEvent(event)
 *
 *     }
 * }
 *
 */