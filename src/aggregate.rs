use std::collections::HashMap;

use chrono::NaiveDate;

use crate::models::Workout;

pub fn aggregate_daily_volume(workouts: &[Workout]) -> HashMap<NaiveDate, f64> {
    let mut volumes: HashMap<NaiveDate, f64> = HashMap::new();

    for workout in workouts {
        let date = workout.start_time.date_naive();
        let mut workout_volume = 0.0;

        for exercise in &workout.exercises {
            for set in &exercise.sets {
                let weight = set.weight_kg.unwrap_or(0.0);
                let reps = set.reps.unwrap_or(0) as f64;
                workout_volume += weight * reps;
            }
        }

        *volumes.entry(date).or_insert(0.0) += workout_volume;
    }

    volumes
}
