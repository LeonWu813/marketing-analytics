import { useState } from "react"
import styles from "../common/SiteCreatedModal.module.css"

interface Props {
  siteCode: string
  siteName: string
  onDone: () => void
}

export default function SiteCreatedModal({ siteCode, siteName, onDone }: Props) {
  const [copied, setCopied] = useState(false)

  const snippet = `<script>
(function() {
  var SITE_CODE = "${siteCode}";
  var API_URL = "https://ezily.io/api/events";

  function getUtmParams() {
    var params = new URLSearchParams(window.location.search);
    return {
      utmSource:   params.get("utm_source")   || undefined,
      utmMedium:   params.get("utm_medium")   || undefined,
      utmCampaign: params.get("utm_campaign") || undefined,
    };
  }

  function sendEvent(eventType, metadata) {
    fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(Object.assign({
        siteCode:  SITE_CODE,
        eventType: eventType,
        pageUrl:   window.location.href,
      }, getUtmParams(), { metadata: metadata || {} }))
    });
  }

  document.addEventListener("click", function(e) {
    if (!e.target) return;
    var target = e.target.closest("button, a, [data-track]")
    if (target) {
      sendEvent("CLICK", { element: target.tagName, text: target.innerText })
    }
  })

  document.addEventListener("submit", function(e) {
    sendEvent("FORM_SUBMIT", { formId: e.target.id })
  })

  window.addEventListener("load", function() {
    sendEvent("PAGE_VIEW");
  });

  window.MarketingTracker = { track: sendEvent };
})();
</script>`

  function handleCopy() {
    navigator.clipboard.writeText(snippet)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  return <div className={styles.backdrop}>
    <div className={styles.modal}>
      <p className="titles">Site: {siteName} is Ready</p>
      <div>
        <p className={styles.instructions}>How to install your tracker:</p>
        <ol>
          <li className={styles.instructions}> Copy the script below</li>
          <li className={styles.instructions}>{`Paste before </head> on your site`}</li>
          <li className={styles.instructions}>Events appear in your dashboard</li>
        </ol>
      </div>
      <pre className={styles.snippet}>{snippet}</pre>
      <div className={styles.buttonContainer}>
        <button onClick={handleCopy} className="button-secondary button-small">{copied ? "Copied!" : "Copy"}</button>
        <button onClick={onDone} className="button-primary button-small">Done</button>
      </div>
    </div>
  </div>
}