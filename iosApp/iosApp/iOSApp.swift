import SwiftUI
import qrscanner


@main
struct iOSApp: App {
    
    init() {
        
        IOSBridgeKt.iosBridge = IOSBridgeImpl()
        
        KoinInitKt.doInitKoin()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
