(ns miner.palmetto
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  )
            
(def ^:dynamic *raw-dom* (html/html-resource (io/resource "confidential/palmetto-players.html")))
(def ^:dynamic *rows* (html/select *raw-dom* [:html :body :table :tbody :tr]))

(def ^:dynamic *headers* (map html/text (html/select *raw-dom* [:html :body :table :thead
                                                                :tr :td])))

(defn raw-keywordize [s]
  (keyword "raw" (str/lower-case (str/replace s \/ \_))))

(def ^:dynamic *raw-keys* (mapv raw-keywordize *headers*))


(defn palm []
  (html/select (first *rows*) [:td]))

;; NO -- WON'T WORK
;; can't just take html/text on nodes because there are <br> for formatting in some fields.
;; Will need to parse those nodes with custom code.
#_ (defn raw-players []
  (map #(zipmap *raw-keys* (map html/text (html/select % [:td]))) *rows*))





;; Crazy formatting
;; Name:  Last,First (Nick)?

(defn parse-name [raw]
  (if (str/ends-with? raw ")")
    (let [nick-beg (str/index-of raw "(")
          nick (subs raw (inc nick-beg) (dec (count raw)))
          [_ last-name first-name] (re-matches #"([^,]+), *(.+)" (subs raw 0 (dec nick-beg)))]
      [::first first-name
       ::last last-name
       ::nick nick])
    (let [[_ last-name first-name] (re-matches #"([^,]+), *(.+)" raw)]
      [::first first-name
       ::last last-name])))


;; all extract-* fns should return seq of kvs (one node can produce multiple fields)
(defn extract-name [node]
  (parse-name (html/text node)))



(comment
(into (sorted-map) (frequencies (map ::club (all-players))))

{"Aiken" 1,
 "Aiken Pickleball" 1,
 "Aiken Pickledilly's" 1,
 "Aiken Pickledillys" 53,
 "Aiken/Coulmiba SC" 1,
 "Athens Area Pickleball Association" 2,
 "Atlanta Pickleball" 4,
 "Augusta" 2,
 "Augusta Ga." 1,
 "Augusta Pickleball,  Augusta Ga" 1,
 "Augusta Y / Odell Week" 1,
 "Augusta, GA" 1,
 "Brevard" 1,
 "Camden Pickleball" 1,
 "Cane Bay Pickleball Club" 9,
 "City of Sugarhill" 1,
 "Connestee Falls Mountain Picklers" 1,
 "Crosswalk" 1,
 "Death Valley Pickleball Gang" 1,
 "FL" 2,
 "GA" 11,
 "Green Boundary" 1,
 "Hanahan" 1,
 "Hanahan and Low Country Senior Centet" 1,
 "Hilton Head Island Rec Center" 1,
 "Keowee Key Pickleball Club" 1,
 "LA" 1,
 "Lake Greenwood" 1,
 "Myrtle Beach Pickleballers" 4,
 "NC" 13,
 "New Bern" 1,
 "New Bern Pickleball Club" 1,
 "North Myrtle Beach Pickleballers" 1,
 "North Myrtle Beach Team Pickleball" 2,
 "Other" 2,
 "Paddle Up Sports" 2,
 "Palmetto Pickleball Club" 5,
 "Palmetto pickleball club" 2,
 "Pee Dee Pickle ball Club, Florence,SC" 1,
 "Pee Dee Picklers" 3,
 "Pee Dee Picklers, Florence SC" 1,
 "Pleasant Grove UMC Pickleball" 1,
 "Rabin County Puckleball" 1,
 "Rabun Co Pickleball" 1,
 "Rawls Creek Paddler" 1,
 "SC" 15,
 "Savannah Lakes Village" 1,
 "Shelby" 2,
 "Shelby City Park" 1,
 "Sun City Carolina Lakes" 7,
 "Sun City Hilton Head" 3,
 "Sun City Peachtree" 1,
 "Sun City Peachtree, GA" 1,
 "Sweetwater by Del Webb" 1,
 "TN" 2,
 "Tellico Village" 1,
 "Tellico Village Pickleball Club" 4,
 "The Landings Club" 1,
 "The Landings, Savannah GA" 1,
 "The Reserve" 1,
 "Upstate Pickleball" 1,
 "Upstate Pickleball Group" 1,
 "WNC Pickleball Club" 2,
 "Wilson Family Y" 2,
 "Wilson Family YMCA" 2,
 "Wilson Family YMCA Augusta" 1,
 "Wilson YMCA" 1,
 "Woodside" 1,
 "Woodside Plantation" 1,
 "odell weeks pickleball club" 1,
 "palmetto pickleball club, columbia sc" 1,
 "shelby" 1}

;; end comment
)

(def club-standards
  #{"Aiken Pickledillys"
    "Athens Area Pickleball Association"    
    "Atlanta Pickleball"
    "Augusta"
    "Brevard"
    "Wilson Family Y"
    "Woodside Plantation"
    "Camden Pickleball"
    "Cane Bay Pickleball Club"
    "City of Sugarhill"
    "Connestee Falls Mountain Picklers"
    "Crosswalk"
    "Death Valley Pickleball Gang"
    "Hilton Head Island Rec Center"
    "Keowee Key Pickleball Club"
    "Lake Greenwood"
    "Myrtle Beach Pickleballers"
    "North Myrtle Beach Pickleballers"
    "Green Boundary"
    "Hanahan"
    "Palmetto Pickleball Club"
    "New Bern Pickleball Club"
    "Paddle Up Sports"
    "Pee Dee Picklers"
    "Rabin County Pickleball"
    "Rawls Creek Paddler"
    "Savannah Lakes Village"
    "Shelby"
    "Sun City Carolina Lakes"
    "Sun City Hilton Head"
    "Sun City Peachtree"
    "Sweetwater by Del Webb"
    "Tellico Village Pickleball Club" 
    "The Landings Club"
    "The Reserve"
    "Upstate Pickleball"
    "WNC Pickleball Club"
    "TN"
    "SC"     
    "NC"
    "LA"
    "FL"
    "GA"
    "Other"
    })


(def club-overrides
  {"Aiken" "Aiken Pickledillys"
   "Aiken Pickleball" "Aiken Pickledillys"
   "Aiken Pickledilly's" "Aiken Pickledillys"
   "Aiken/Coulmiba SC" "Aiken Pickledillys"
   "Augusta Ga." "Augusta"
   "Augusta Pickleball,  Augusta Ga" "Augusta"
   "Augusta Y / Odell Week" "Wilson Family Y"
   "Augusta, GA" "Augusta"
   "Hanahan and Low Country Senior Centet" "Hanahan"
   "New Bern"  "New Bern Pickleball Club" 
   "North Myrtle Beach Team Pickleball"      "North Myrtle Beach Pickleballers"
   "Palmetto pickleball club"  "Palmetto Pickleball Club"
   "Pee Dee Pickle ball Club, Florence,SC"     "Pee Dee Picklers"
   "Pee Dee Picklers, Florence SC"     "Pee Dee Picklers"
   "Pleasant Grove UMC Pickleball"     "Pee Dee Picklers"
   "Rabin County Puckleball"  "Rabin County Pickleball"
   "Rabun Co Pickleball" "Rabin County Pickleball"
   "Shelby City Park" "Shelby"
   "Sun City Peachtree, GA"  "Sun City Peachtree"
   "Tellico Village" "Tellico Village Pickleball Club" 
   "The Landings, Savannah GA"  "The Landings Club"
   "Upstate Pickleball Group"  "Upstate Pickleball"
   "Wilson Family YMCA" "Wilson Family Y"
   "Wilson Family YMCA Augusta" "Wilson Family Y"
   "Wilson YMCA" "Wilson Family Y"
   "Woodside" "Woodside Plantation"
   "odell weeks pickleball club" "Aiken Pickledillys"
   "palmetto pickleball club, columbia sc" "Palmetto Pickleball Club"
   "shelby" "Shelby"
   })



;; SEM FIXME: need to canonicalize clubs
(defn extract-club [node]
  (let [reported (html/text node)
        club (or (club-standards reported) (club-overrides reported))]
    (if club
      [::club club]
      [::reported-club reported])))

(defn extract-gender [node]
  (let [gend (html/text node)]
    [::gender (if (= gend "M") :male :female)]))

(defn extract-age [node]
  (let [agestr (html/text node)]
    [::age (Long/parseLong agestr)]))

;; SEM FIX ME canonicalize city, state and phone

;; vector lines
;; phone-like last, else city-st last
;; previous addr, maybe addr2

;; SEM FIXME: phone should strip non-digits, make sure there are ten and reformat 3-3-4 style
(defn phone-like? [s]
  (re-matches #"[(]?\d{3}[) .-]+\d{3}[-.]?\d{4}" s))

(defn canonical-phone [s]
  (let [dchars (set "0123456789")
        digits (filterv dchars s)]
    (when (= (count digits) 10)
      (str (apply str (subvec digits 0 3))
           "-"
           (apply str (subvec digits 3 6))
           "-"
           (apply str (subvec digits 6 10))))))

(defn parse-addr [lines]
  (case (count lines)
    2 (let [[addr city-st] lines
            comma (str/index-of city-st ",")
            city (subs city-st 0 comma)
            st (str/trim (subs city-st (inc comma)))]
        [::addr addr ::city city ::state st])
    3  (let [[addr addr2 city-st] lines
             comma (str/index-of city-st ",")
             city (subs city-st 0 comma)
             st (str/trim (subs city-st (inc comma)))]
         [::addr addr ::addr2 addr2 ::city city ::state st])
    [::BAD-ADDR (apply str/join " / "lines)]))
          
;; ZIP missing from data -- need different report???
(defn extract-address-phone [node]
  (let [vlines (filterv string? (:content node))
        ph1 (peek vlines)]
    (if (phone-like? ph1)
      (into (parse-addr (pop vlines)) [::phone (canonical-phone ph1)])
      (parse-addr vlines))))
    
(defn extract-skill [node]
  (let [skstr (html/text node)
        dot (when skstr (str/index-of skstr "."))
        sk (if dot (Double/parseDouble (subs skstr 0 (+ dot 2))) 0.0)]
    [::skill sk]))


(defn parse-event-partner [s]
  (when s
    (let [[ev partner] (str/split s #"[-]")]
      (case ev
        "MD" [::md partner]
        "MXD" [::mxd partner]
        "WD" [::wd partner]
        [::mystery-event s]))))


;; SEM FIXME -- need to extract MD-partner, t-shirts, etc
;; look for <BR>
(defn extract-events [node]
  (let [[reg evp1 evp2] (filter string? (:content node))
        [ev1 p1] (parse-event-partner evp1)
        [ev2 p2] (parse-event-partner evp2)
        pro-am (str/includes? reg "PAPLAY")
        clin1 (str/includes? reg "CLIN1")
        clin2 (str/includes? reg "CLIN2")
        lesson1 (str/includes? reg "LESSON1")
        [gwd gnum] (re-find #"GWD[(](\d)[)]" reg)
        tshirt (re-find #"[MW]FTS[(]\S+[)]" reg)]
    (cond-> []
      ev1 (conj ev1 p1)
      ev2 (conj ev2 p2)
      pro-am (conj ::pro-am true)
      clin1 (conj ::clinic1 true)
      clin2 (conj ::clinic2 true)
      lesson1 (conj ::lesson1 true)
      gwd (conj ::gwd gnum)
      tshirt (conj ::tshirt tshirt))))

(defn clean-nbsp [s]
  (str/replace s "\u00a0" ""))

(defn extract-fees [node]
  ;; unfinished
  [::fees (clean-nbsp (html/text node))])


(defn extract-paid [node]
  ;; unfinished
  [::paid (clean-nbsp (html/text node))])


(defn extract-due [node]
  ;; unfinished
  (when-not (= "\u00a0" (html/text node))
    [::due (clean-nbsp (html/text node))]))

;; waiver? mostly ignorable
(defn extract-wvr [node]
  (when-not (str/starts-with? (html/text node) "Y")
    [::wvr (html/text node)]))

;; what's T?  mostly ignorable
(defn extract-t [node]
  (when-not (= (html/text node) "P")
    [::t (html/text node)]))

;; what's CI?  probably "checked in".  Will ignore for now
(defn extract-ci [node]
  ;; [::ci (html/text node)]
  nil)






(defn clean-row [row-nodes]
  ;; nodes in order of *headers*
  (apply hash-map (mapcat #(% %2)
                          (list extract-name extract-club extract-gender
                                extract-age extract-address-phone  extract-skill
                                extract-events extract-fees extract-paid extract-due
                                extract-wvr extract-t extract-ci)
                          row-nodes)))


(defn all-players []
  (remove ::BAD-ADDR (map #(clean-row (html/select % [:td])) *rows*)))

(defn dump []
  (doseq [x (filter ::gwd (all-players))]
    (println (:miner.palmetto/first x) (:miner.palmetto/last x)
             (str "(" (:miner.palmetto/club x) "), gwd") (:miner.palmetto/gwd x))))

(def all-keys
  [:miner.palmetto/first :miner.palmetto/last :miner.palmetto/nick 
   :miner.palmetto/gender   :miner.palmetto/age :miner.palmetto/skill
   :miner.palmetto/club 
   :miner.palmetto/fees :miner.palmetto/paid :miner.palmetto/tshirt
   :miner.palmetto/pro-am  :miner.palmetto/lesson1 :miner.palmetto/clinic1
   :miner.palmetto/clinic2 :miner.palmetto/gwd 
   :miner.palmetto/mxd :miner.palmetto/md  :miner.palmetto/wd 
   :miner.palmetto/addr :miner.palmetto/addr2
   :miner.palmetto/city  :miner.palmetto/state
   :miner.palmetto/phone ])

(def vectorizer (apply juxt all-keys))

(defn write-csv-file
  ([] (write-csv-file "/tmp/players.csv"))
  ([to]
   (with-open [writer (io/writer to)]
     (let [all (all-players)]
       (csv/write-csv writer (conj (map vectorizer (all-players)) (map name all-keys)))))
     to))

;; GWD = Gathering with Dinner
;; many non-players attended, look for non-players reports

;; also pro-am got non-players
;; probably volunteers are also in system

