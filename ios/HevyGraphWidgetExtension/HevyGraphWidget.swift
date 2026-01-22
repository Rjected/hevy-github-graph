import WidgetKit
import SwiftUI

struct Provider: TimelineProvider {
    func placeholder(in context: Context) -> WorkoutEntry {
        WorkoutEntry(date: Date(), dailyVolumes: [:], theme: .blue)
    }
    
    func getSnapshot(in context: Context, completion: @escaping (WorkoutEntry) -> Void) {
        let volumes = DataStore.shared.loadDailyVolumes()
        let theme = DataStore.shared.getColorTheme()
        let entry = WorkoutEntry(date: Date(), dailyVolumes: volumes, theme: theme)
        completion(entry)
    }
    
    func getTimeline(in context: Context, completion: @escaping (Timeline<WorkoutEntry>) -> Void) {
        let volumes = DataStore.shared.loadDailyVolumes()
        let theme = DataStore.shared.getColorTheme()
        let entry = WorkoutEntry(date: Date(), dailyVolumes: volumes, theme: theme)
        
        let nextUpdate = Calendar.current.date(byAdding: .hour, value: 1, to: Date()) ?? Date()
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdate))
        completion(timeline)
    }
}

struct WorkoutEntry: TimelineEntry {
    let date: Date
    let dailyVolumes: [Date: Double]
    let theme: ColorTheme
}

struct HevyGraphWidgetEntryView: View {
    var entry: Provider.Entry
    @Environment(\.widgetFamily) var family
    
    var body: some View {
        ZStack {
            Color(red: 22/255, green: 24/255, blue: 29/255)
            
            ContributionGraphView(
                dailyVolumes: entry.dailyVolumes,
                theme: entry.theme,
                weeks: weeksForFamily
            )
            .padding(8)
        }
    }
    
    private var weeksForFamily: Int {
        switch family {
        case .systemSmall:
            return 7
        case .systemMedium:
            return 15
        case .systemLarge:
            return 20
        case .systemExtraLarge:
            return 26
        default:
            return 15
        }
    }
}

struct HevyGraphWidget: Widget {
    let kind: String = "HevyGraphWidget"
    
    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            HevyGraphWidgetEntryView(entry: entry)
                .containerBackground(.fill.tertiary, for: .widget)
        }
        .configurationDisplayName("Workout Graph")
        .description("Display your Hevy workout history as a GitHub-style contribution graph.")
        .supportedFamilies([.systemSmall, .systemMedium, .systemLarge])
    }
}

#Preview(as: .systemMedium) {
    HevyGraphWidget()
} timeline: {
    WorkoutEntry(date: Date(), dailyVolumes: [:], theme: .blue)
}
