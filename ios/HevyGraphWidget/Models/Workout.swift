import Foundation

struct WorkoutsResponse: Codable {
    let page: Int
    let pageCount: Int
    let workouts: [Workout]
    
    enum CodingKeys: String, CodingKey {
        case page
        case pageCount = "page_count"
        case workouts
    }
}

struct Workout: Codable, Identifiable {
    let id: String
    let title: String
    let startTime: String
    let endTime: String
    let exercises: [Exercise]
    
    enum CodingKeys: String, CodingKey {
        case id, title, exercises
        case startTime = "start_time"
        case endTime = "end_time"
    }
    
    var startDate: Date? {
        ISO8601DateFormatter().date(from: startTime)
    }
}

struct Exercise: Codable {
    let title: String
    let exerciseTemplateId: String
    let sets: [ExerciseSet]
    
    enum CodingKeys: String, CodingKey {
        case title, sets
        case exerciseTemplateId = "exercise_template_id"
    }
}

struct ExerciseSet: Codable {
    let type: String
    let weightKg: Double?
    let reps: Int?
    let distanceMeters: Double?
    let durationSeconds: Int?
    let rpe: Double?
    
    enum CodingKeys: String, CodingKey {
        case type, reps, rpe
        case weightKg = "weight_kg"
        case distanceMeters = "distance_meters"
        case durationSeconds = "duration_seconds"
    }
}
