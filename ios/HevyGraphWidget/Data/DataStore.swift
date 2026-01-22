import Foundation

struct DailyVolumeEntry: Codable {
    let date: Date
    let volume: Double
}

class DataStore {
    static let shared = DataStore()
    
    private let userDefaults: UserDefaults?
    private let volumesKey = "dailyVolumes"
    
    private init() {
        self.userDefaults = UserDefaults(suiteName: "group.com.hevy.graphwidget")
    }
    
    func saveDailyVolumes(_ volumes: [Date: Double]) throws {
        let entries = volumes.map { DailyVolumeEntry(date: $0.key, volume: $0.value) }
        let data = try JSONEncoder().encode(entries)
        userDefaults?.set(data, forKey: volumesKey)
    }
    
    func loadDailyVolumes() -> [Date: Double] {
        guard let data = userDefaults?.data(forKey: volumesKey) else {
            return [:]
        }
        
        do {
            let entries = try JSONDecoder().decode([DailyVolumeEntry].self, from: data)
            return Dictionary(uniqueKeysWithValues: entries.map { ($0.date, $0.volume) })
        } catch {
            return [:]
        }
    }
    
    func getApiKey() -> String? {
        userDefaults?.string(forKey: "apiKey")
    }
    
    func getColorTheme() -> ColorTheme {
        let themeName = userDefaults?.string(forKey: "colorTheme") ?? "blue"
        return ColorTheme(rawValue: themeName) ?? .blue
    }
}
