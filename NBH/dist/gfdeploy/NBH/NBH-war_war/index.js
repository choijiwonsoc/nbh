// Initialize and add the map
let map;

async function initMap() {
    // The location of the center of the map
    const centerPosition = {lat: 1.35, lng: 103.85};

    // Request needed libraries.
    //@ts-ignore
    const {Map} = await google.maps.importLibrary("maps");

    // Create the map, centered at the specified position
    map = new Map(document.getElementById("map"), {
        zoom: 11,
        center: centerPosition,
        mapId: "DEMO_MAP_ID",
    });

    // Fetch postal codes from the managed bean using Ajax
    fetchPostalCodes();
}

// Function to fetch postal codes from the managed bean using Ajax
async function fetchPostalCodes() {
    try {
//        const response = await fetch(`${hiddenGemManagedBean.getAllPostalCode}`);
//        if (!response.ok) {
//            throw new Error("Failed to fetch postal codes.");
//        }
//        const postalCodes = await response.json();
        const postalCodes = ['138597', '138598'];

        // Iterate through the postal codes and create markers for each
        postalCodes.forEach(async (postalCode) => {
            const position = await geocodePostalCode(postalCode); // Geocode the postal code to get its position
            if (position) {
                new google.maps.Marker({
                    position: position,
                    map: map,
                    title: postalCode,
                });
            }
        });
    } catch (error) {
        console.error("Error fetching postal codes:", error);
    }
}

async function geocodePostalCode(postalCode) {
    try {
        const apiKey = "AIzaSyCGSsXh_j_9idjpolfxyjx_kWt4_7Bk1i4"; // Replace with your Google Maps API key
        const response = await fetch(`https://maps.googleapis.com/maps/api/geocode/json?address=${postalCode}&key=${apiKey}`);
        if (!response.ok) {
            throw new Error("Failed to geocode postal code.");
        }
        const data = await response.json();
        console.log("Response data:", data); // Log the parsed data
        if (data.results && data.results.length > 0) {
            const location = data.results[0].geometry.location;
            return {lat: location.lat, lng: location.lng};
        } else {
            console.error("No results found for postal code:", postalCode);
            return null;
        }
    } catch (error) {
        console.error("Error geocoding postal code:", error);
        return null;
    }
}


initMap(); // Call the initMap function to initialize the map
