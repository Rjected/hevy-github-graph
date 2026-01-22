import Foundation

enum HevyClientError: LocalizedError {
    case invalidURL
    case apiError(statusCode: Int, message: String)
    case decodingError(Error)
    
    var errorDescription: String? {
        switch self {
        case .invalidURL:
            return "Invalid URL"
        case .apiError(let statusCode, let message):
            return "API error (\(statusCode)): \(message)"
        case .decodingError(let error):
            return "Decoding error: \(error.localizedDescription)"
        }
    }
}

actor HevyClient {
    static let shared = HevyClient()
    
    private let baseURL = "https://api.hevyapp.com"
    private let pageSize = 10
    private let session: URLSession
    
    private init() {
        let config = URLSessionConfiguration.default
        config.timeoutIntervalForRequest = 30
        self.session = URLSession(configuration: config)
    }
    
    func fetchAllWorkouts(apiKey: String) async throws -> [Workout] {
        var allWorkouts: [Workout] = []
        var page = 1
        var pageCount = 1
        
        while page <= pageCount {
            let response = try await fetchWorkoutsPage(apiKey: apiKey, page: page)
            allWorkouts.append(contentsOf: response.workouts)
            pageCount = response.pageCount
            page += 1
        }
        
        return allWorkouts
    }
    
    private func fetchWorkoutsPage(apiKey: String, page: Int) async throws -> WorkoutsResponse {
        guard let url = URL(string: "\(baseURL)/v1/workouts?page=\(page)&pageSize=\(pageSize)") else {
            throw HevyClientError.invalidURL
        }
        
        var request = URLRequest(url: url)
        request.setValue(apiKey, forHTTPHeaderField: "api-key")
        
        let (data, response) = try await session.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw HevyClientError.apiError(statusCode: 0, message: "Invalid response")
        }
        
        guard httpResponse.statusCode == 200 else {
            let message = String(data: data, encoding: .utf8) ?? "Unknown error"
            throw HevyClientError.apiError(statusCode: httpResponse.statusCode, message: message)
        }
        
        do {
            let decoder = JSONDecoder()
            return try decoder.decode(WorkoutsResponse.self, from: data)
        } catch {
            throw HevyClientError.decodingError(error)
        }
    }
}
