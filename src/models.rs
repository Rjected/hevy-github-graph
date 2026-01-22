use chrono::{DateTime, FixedOffset};
use serde::Deserialize;

#[derive(Debug, Deserialize)]
pub struct WorkoutsResponse {
    pub page: i32,
    pub page_count: i32,
    pub workouts: Vec<Workout>,
}

#[derive(Debug, Deserialize)]
pub struct Workout {
    pub id: String,
    pub title: String,
    pub start_time: DateTime<FixedOffset>,
    pub end_time: DateTime<FixedOffset>,
    pub exercises: Vec<Exercise>,
}

#[derive(Debug, Deserialize)]
pub struct Exercise {
    pub title: String,
    pub exercise_template_id: String,
    pub sets: Vec<ExerciseSet>,
}

#[derive(Debug, Deserialize)]
pub struct ExerciseSet {
    #[serde(rename = "type")]
    pub set_type: String,
    pub weight_kg: Option<f64>,
    pub reps: Option<i32>,
    pub distance_meters: Option<f64>,
    pub duration_seconds: Option<i32>,
    pub rpe: Option<f64>,
}
