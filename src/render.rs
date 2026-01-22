use std::collections::HashMap;
use std::io::{Write, stdout};

use chrono::{Datelike, Duration, Local, NaiveDate};
use crossterm::style::{Color, ResetColor, SetBackgroundColor};

use crate::normalize::compute_intensity_levels;

const COLORS: [Color; 5] = [
    Color::AnsiValue(236),  // 0: dark gray (no workout)
    Color::AnsiValue(22),   // 1: dark green
    Color::AnsiValue(28),   // 2: green
    Color::AnsiValue(34),   // 3: bright green
    Color::AnsiValue(46),   // 4: brightest green
];

const ASCII_CHARS: [char; 5] = ['.', '░', '▒', '▓', '█'];

const DAY_LABELS: [&str; 7] = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

/// Render a GitHub-style contribution graph to the terminal.
pub fn render_graph(daily_volumes: &HashMap<NaiveDate, f64>, weeks: usize, ascii_mode: bool) {
    let today = Local::now().date_naive();
    
    // End on today, start from (weeks) weeks ago aligned to Sunday
    // The rightmost column contains the current partial week ending on today
    let today_dow = today.weekday().num_days_from_sunday() as i64;
    
    // Total days to show: (weeks-1) full weeks + days in current week (0=Sun through today)
    let total_days = (weeks as i64 - 1) * 7 + today_dow + 1;
    let start_date = today - Duration::days(total_days - 1);

    // Build grid - each column is a week (Sun-Sat), rightmost column ends on today
    let mut grid: Vec<Vec<Option<NaiveDate>>> = vec![vec![None; weeks]; 7];
    
    for day_offset in 0..total_days {
        let date = start_date + Duration::days(day_offset);
        let col = (day_offset / 7) as usize;
        let row = date.weekday().num_days_from_sunday() as usize;
        if col < weeks && row < 7 {
            grid[row][col] = Some(date);
        }
    }

    // Collect volumes in row-major order for normalization
    let mut all_volumes: Vec<f64> = Vec::new();
    let mut volume_positions: Vec<(usize, usize)> = Vec::new();
    
    for (row, week_row) in grid.iter().enumerate() {
        for (col, date_opt) in week_row.iter().enumerate() {
            if let Some(date) = date_opt {
                let vol = *daily_volumes.get(date).unwrap_or(&0.0);
                all_volumes.push(vol);
                volume_positions.push((row, col));
            }
        }
    }

    // Compute intensity levels
    let levels = compute_intensity_levels(&all_volumes);

    // Build intensity grid
    let mut intensity_grid: Vec<Vec<u8>> = vec![vec![0; weeks]; 7];
    for (i, &(row, col)) in volume_positions.iter().enumerate() {
        intensity_grid[row][col] = levels[i];
    }

    // Render
    let mut out = stdout();
    println!();

    for (row, day_label) in DAY_LABELS.iter().enumerate() {
        // Only show labels for Mon, Wed, Fri to reduce clutter (like GitHub)
        if row == 1 || row == 3 || row == 5 {
            print!("{} ", day_label);
        } else {
            print!("    ");
        }

        for col in 0..weeks {
            if grid[row][col].is_some() {
                let level = intensity_grid[row][col] as usize;
                if ascii_mode {
                    let ch = ASCII_CHARS[level];
                    print!("{}", ch);
                } else {
                    let color = COLORS[level];
                    write!(out, "{}", SetBackgroundColor(color)).unwrap();
                    write!(out, "  ").unwrap();
                    write!(out, "{}", ResetColor).unwrap();
                }
            } else {
                if ascii_mode {
                    print!(" ");
                } else {
                    print!("  ");
                }
            }
        }
        println!();
    }

    // Legend
    println!();
    print!("     Less ");
    if ascii_mode {
        for ch in &ASCII_CHARS {
            print!("{}", ch);
        }
    } else {
        for color in &COLORS {
            write!(out, "{}", SetBackgroundColor(*color)).unwrap();
            write!(out, "  ").unwrap();
            write!(out, "{}", ResetColor).unwrap();
        }
    }
    println!(" More");

    let workout_days: usize = all_volumes.iter().filter(|v| **v > 0.0).count();
    println!("     {} days with workouts shown", workout_days);
    println!();
}
