import SwiftUI

enum ColorTheme: String, CaseIterable, Identifiable {
    case blue
    case green
    case purple
    case orange
    
    var id: String { rawValue }
    
    var displayName: String {
        rawValue.capitalized
    }
    
    var colors: [Color] {
        switch self {
        case .blue:
            return [
                Color(red: 35/255, green: 38/255, blue: 45/255),     // 0: no workout
                Color(red: 140/255, green: 180/255, blue: 255/255), // 1: lightest
                Color(red: 80/255, green: 130/255, blue: 220/255),  // 2
                Color(red: 40/255, green: 90/255, blue: 180/255),   // 3
                Color(red: 15/255, green: 50/255, blue: 120/255)    // 4: darkest
            ]
        case .green:
            return [
                Color(red: 35/255, green: 38/255, blue: 45/255),
                Color(red: 140/255, green: 230/255, blue: 160/255),
                Color(red: 80/255, green: 180/255, blue: 100/255),
                Color(red: 40/255, green: 130/255, blue: 60/255),
                Color(red: 15/255, green: 80/255, blue: 30/255)
            ]
        case .purple:
            return [
                Color(red: 35/255, green: 38/255, blue: 45/255),
                Color(red: 200/255, green: 160/255, blue: 255/255),
                Color(red: 160/255, green: 110/255, blue: 220/255),
                Color(red: 120/255, green: 70/255, blue: 180/255),
                Color(red: 70/255, green: 30/255, blue: 120/255)
            ]
        case .orange:
            return [
                Color(red: 35/255, green: 38/255, blue: 45/255),
                Color(red: 255/255, green: 200/255, blue: 140/255),
                Color(red: 230/255, green: 150/255, blue: 80/255),
                Color(red: 200/255, green: 100/255, blue: 40/255),
                Color(red: 140/255, green: 60/255, blue: 15/255)
            ]
        }
    }
    
    func color(for level: Int) -> Color {
        let index = max(0, min(level, colors.count - 1))
        return colors[index]
    }
}
