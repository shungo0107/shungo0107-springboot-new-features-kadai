 const stripe = Stripe('pk_test_51QJ65DAcYK9h7ZJRbd5YvEWgseZtDlB7dVy3l9WRlbGTJcRGi7vhbwrWI00Bc7hpo39TQTiEpQGmdHyd6eCs61ff00GHY1vNA3');
 const paymentButton = document.querySelector('#paymentButton');
 
 paymentButton.addEventListener('click', () => {
   stripe.redirectToCheckout({
     sessionId: sessionId
   })
 });