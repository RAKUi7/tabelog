let maxDate = new Date();
maxDate = maxDate.setMonth(maxDate.getMonth() + 3);

flatpickr('#reservedDatetime', {
	locale: 'ja',
	minDate: 'today',
	maxDate: maxDate
});