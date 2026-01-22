use crate::models::{Workout, WorkoutsResponse};

const BASE_URL: &str = "https://api.hevyapp.com";
const PAGE_SIZE: i32 = 10;

pub struct HevyClient {
    client: reqwest::Client,
    api_key: String,
}

impl HevyClient {
    pub fn new(api_key: String) -> Self {
        Self {
            client: reqwest::Client::new(),
            api_key,
        }
    }

    pub async fn fetch_all_workouts(&self) -> Result<Vec<Workout>, Box<dyn std::error::Error>> {
        let mut all_workouts = Vec::new();
        let mut page = 1;

        loop {
            let url = format!("{}/v1/workouts?page={}&pageSize={}", BASE_URL, page, PAGE_SIZE);
            let response = self
                .client
                .get(&url)
                .header("api-key", &self.api_key)
                .send()
                .await?;

            let status = response.status();
            let body = response.text().await?;

            if !status.is_success() {
                return Err(format!("API error ({}): {}", status, body).into());
            }

            let parsed: WorkoutsResponse = serde_json::from_str(&body)
                .map_err(|e| format!("Failed to parse response: {}. Body: {}", e, &body[..body.len().min(200)]))?;

            all_workouts.extend(parsed.workouts);

            if page >= parsed.page_count {
                break;
            }
            page += 1;
        }

        Ok(all_workouts)
    }
}
