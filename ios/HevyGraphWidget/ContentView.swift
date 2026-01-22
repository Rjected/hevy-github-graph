import SwiftUI
import WidgetKit

struct ContentView: View {
    @AppStorage("apiKey", store: UserDefaults(suiteName: "group.com.hevy.graphwidget"))
    private var apiKey: String = ""
    
    @AppStorage("colorTheme", store: UserDefaults(suiteName: "group.com.hevy.graphwidget"))
    private var colorTheme: String = "blue"
    
    @State private var isSyncing = false
    @State private var statusMessage = ""
    @State private var workoutCount = 0
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("API Configuration")) {
                    SecureField("Hevy API Key", text: $apiKey)
                        .textContentType(.password)
                        .autocapitalization(.none)
                }
                
                Section(header: Text("Appearance")) {
                    Picker("Color Theme", selection: $colorTheme) {
                        ForEach(ColorTheme.allCases) { theme in
                            Text(theme.displayName).tag(theme.rawValue)
                        }
                    }
                }
                
                Section(header: Text("Preview")) {
                    HStack {
                        Spacer()
                        ContributionGraphView(
                            dailyVolumes: [:],
                            theme: ColorTheme(rawValue: colorTheme) ?? .blue
                        )
                        .frame(height: 120)
                        Spacer()
                    }
                }
                
                Section {
                    Button(action: syncWorkouts) {
                        HStack {
                            if isSyncing {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle())
                            }
                            Text(isSyncing ? "Syncing..." : "Sync Workouts")
                        }
                    }
                    .disabled(apiKey.isEmpty || isSyncing)
                    
                    if !statusMessage.isEmpty {
                        Text(statusMessage)
                            .font(.caption)
                            .foregroundColor(statusMessage.contains("Error") ? .red : .green)
                    }
                }
                
                Section(header: Text("Instructions")) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("1. Get your API key from Hevy Settings → Developer Settings")
                        Text("2. Enter your API key above")
                        Text("3. Tap 'Sync Workouts' to fetch your data")
                        Text("4. Add the widget to your home screen")
                    }
                    .font(.caption)
                    .foregroundColor(.secondary)
                }
            }
            .navigationTitle("Hevy Graph Widget")
        }
    }
    
    private func syncWorkouts() {
        guard !apiKey.isEmpty else { return }
        
        isSyncing = true
        statusMessage = ""
        
        Task {
            do {
                let workouts = try await HevyClient.shared.fetchAllWorkouts(apiKey: apiKey)
                let dailyVolumes = WorkoutAggregator.aggregateDailyVolumes(workouts: workouts)
                
                try DataStore.shared.saveDailyVolumes(dailyVolumes)
                
                await MainActor.run {
                    workoutCount = workouts.count
                    statusMessage = "Synced \(workouts.count) workouts!"
                    isSyncing = false
                    WidgetCenter.shared.reloadAllTimelines()
                }
            } catch {
                await MainActor.run {
                    statusMessage = "Error: \(error.localizedDescription)"
                    isSyncing = false
                }
            }
        }
    }
}

#Preview {
    ContentView()
}
