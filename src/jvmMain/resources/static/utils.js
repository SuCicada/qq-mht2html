document.addEventListener("DOMContentLoaded", function () {
console.log("start utils.js")
    qqtsElements = document.querySelectorAll('div.qqts');

    function formatDateWithTimeZone(date) {
        const year = date.getUTCFullYear();
        const month = String(date.getUTCMonth() + 1).padStart(2, "0");
        const day = String(date.getUTCDate()).padStart(2, "0");
        const hours = String(date.getUTCHours()).padStart(2, "0");
        const minutes = String(date.getUTCMinutes()).padStart(2, "0");
        const seconds = String(date.getUTCSeconds()).padStart(2, "0");
        const timeZoneOffset = -date.getTimezoneOffset();
        const timeZoneHours = String(Math.floor(timeZoneOffset / 60)).padStart(2, "0");
        const timeZoneMinutes = String(Math.abs(timeZoneOffset) % 60).padStart(2, "0");
        const timeZone = timeZoneOffset >= 0 ? `+${timeZoneHours}` : `-${timeZoneHours}`;

        return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}${timeZone}`;
    }

// 遍历每个元素
    qqtsElements.forEach(element => {
        const now = new Date();
        const formattedDate = formatDateWithTimeZone(now);
        element.parentElement.childNodes[1].textContent = formattedDate;
    });

})
