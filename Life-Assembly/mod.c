int mod(int dividend, int divisor) {
	while (dividend > divisor) {
		dividend -= divisor;
	}
	return dividend;
}
