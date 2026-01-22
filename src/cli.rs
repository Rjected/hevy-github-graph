use clap::Parser;

#[derive(Parser, Debug)]
#[command(name = "hevy-graph")]
#[command(about = "Display a GitHub-style contribution graph of your Hevy workouts")]
pub struct Args {
    /// Hevy API key
    #[arg(short, long, env = "HEVY_API_KEY")]
    pub api_key: String,
    
    /// Number of weeks to display (default: 52)
    #[arg(short, long, default_value = "52")]
    pub weeks: usize,

    /// Use ASCII characters instead of colors
    #[arg(long, default_value = "false")]
    pub ascii: bool,
}
