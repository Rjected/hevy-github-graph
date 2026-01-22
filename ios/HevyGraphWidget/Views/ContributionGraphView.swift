import SwiftUI

struct ContributionGraphView: View {
    let dailyVolumes: [Date: Double]
    let theme: ColorTheme
    let weeks: Int
    
    private let days = 7
    private let cellSize: CGFloat = 10
    private let cellGap: CGFloat = 2
    private let cornerRadius: CGFloat = 2
    
    init(dailyVolumes: [Date: Double], theme: ColorTheme, weeks: Int = 15) {
        self.dailyVolumes = dailyVolumes
        self.theme = theme
        self.weeks = weeks
    }
    
    var body: some View {
        let grid = buildGrid()
        let levels = computeLevels(grid: grid)
        
        HStack(spacing: cellGap) {
            ForEach(0..<weeks, id: \.self) { week in
                VStack(spacing: cellGap) {
                    ForEach(0..<days, id: \.self) { day in
                        let index = week * days + day
                        if index < levels.count {
                            RoundedRectangle(cornerRadius: cornerRadius)
                                .fill(theme.color(for: levels[index]))
                                .frame(width: cellSize, height: cellSize)
                        } else {
                            Color.clear
                                .frame(width: cellSize, height: cellSize)
                        }
                    }
                }
            }
        }
    }
    
    private func buildGrid() -> [(date: Date, volume: Double)] {
        let calendar = Calendar.current
        let today = Date()
        
        let todayWeekday = calendar.component(.weekday, from: today)
        let sundayBasedWeekday = todayWeekday - 1
        let daysToShow = (weeks - 1) * 7 + sundayBasedWeekday + 1
        
        guard let startDate = calendar.date(byAdding: .day, value: -(daysToShow - 1), to: today) else {
            return []
        }
        
        var grid: [(date: Date, volume: Double)] = []
        
        for dayOffset in 0..<daysToShow {
            guard let date = calendar.date(byAdding: .day, value: dayOffset, to: startDate) else {
                continue
            }
            let dateKey = calendar.startOfDay(for: date)
            let volume = dailyVolumes[dateKey] ?? 0
            grid.append((date: dateKey, volume: volume))
        }
        
        return grid
    }
    
    private func computeLevels(grid: [(date: Date, volume: Double)]) -> [Int] {
        let volumes = grid.map { $0.volume }
        return WorkoutAggregator.computeIntensityLevels(volumes: volumes)
    }
}

#Preview {
    let calendar = Calendar.current
    var volumes: [Date: Double] = [:]
    
    for i in 0..<100 {
        if let date = calendar.date(byAdding: .day, value: -i, to: Date()) {
            let dateKey = calendar.startOfDay(for: date)
            if Int.random(in: 0...2) > 0 {
                volumes[dateKey] = Double.random(in: 1000...10000)
            }
        }
    }
    
    return VStack(spacing: 20) {
        ContributionGraphView(dailyVolumes: volumes, theme: .blue)
        ContributionGraphView(dailyVolumes: volumes, theme: .green)
        ContributionGraphView(dailyVolumes: volumes, theme: .purple)
        ContributionGraphView(dailyVolumes: volumes, theme: .orange)
    }
    .padding()
    .background(Color.black)
}
