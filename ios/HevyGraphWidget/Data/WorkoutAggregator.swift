import Foundation

struct WorkoutAggregator {
    static func aggregateDailyVolumes(workouts: [Workout]) -> [Date: Double] {
        var volumeByDate: [Date: Double] = [:]
        let calendar = Calendar.current
        
        for workout in workouts {
            guard let startDate = workout.startDate else { continue }
            let date = calendar.startOfDay(for: startDate)
            
            var workoutVolume: Double = 0
            for exercise in workout.exercises {
                for set in exercise.sets {
                    let weight = set.weightKg ?? 0
                    let reps = Double(set.reps ?? 0)
                    workoutVolume += weight * reps
                }
            }
            
            volumeByDate[date, default: 0] += workoutVolume
        }
        
        return volumeByDate
    }
    
    static func computeIntensityLevels(volumes: [Double]) -> [Int] {
        let nonZero = volumes.filter { $0 > 0 }.sorted()
        
        if nonZero.isEmpty {
            return volumes.map { _ in 0 }
        }
        
        let q1 = percentile(sorted: nonZero, p: 25)
        let q2 = percentile(sorted: nonZero, p: 50)
        let q3 = percentile(sorted: nonZero, p: 75)
        
        return volumes.map { v in
            if v <= 0 {
                return 0
            } else if v <= q1 {
                return 1
            } else if v <= q2 {
                return 2
            } else if v <= q3 {
                return 3
            } else {
                return 4
            }
        }
    }
    
    private static func percentile(sorted: [Double], p: Double) -> Double {
        guard !sorted.isEmpty else { return 0 }
        guard sorted.count > 1 else { return sorted[0] }
        
        let rank = (p / 100.0) * Double(sorted.count - 1)
        let lower = Int(rank.rounded(.down))
        let upper = Int(rank.rounded(.up))
        let frac = rank - Double(lower)
        
        if lower == upper {
            return sorted[lower]
        } else {
            return sorted[lower] * (1.0 - frac) + sorted[upper] * frac
        }
    }
}
