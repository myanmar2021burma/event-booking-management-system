# 🌐 Hosting & Database Complete Guide

## 📊 Understanding How It Works

### Current Setup (Local Development)
```
┌─────────────────────────────────────────────┐
│  YOUR COMPUTER (localhost)                  │
│                                             │
│  ┌──────────────┐      ┌────────────────┐ │
│  │   Website    │ ←──→ │  MySQL Database│ │
│  │ (Port 7070)  │      │  (Port 3306)   │ │
│  └──────────────┘      └────────────────┘ │
│                                             │
│  Data stored at: /usr/local/mysql/data/    │
└─────────────────────────────────────────────┘
```

---

## ✅ Question 1: Is Website Editing Reflecting in Database?

### Test It Now:

**Step 1: Check current database**
```bash
cd event-management-system
./check_database.sh
```

**Step 2: Make a booking on website**
1. Open: http://localhost:7070/event-management
2. Click "Book Now" on any event
3. Fill the form and submit

**Step 3: Verify in database**
```bash
mysql -u root -p -e "SELECT * FROM event_management.bookings ORDER BY booking_date DESC LIMIT 1;"
```

**✓ If you see your booking, it's working!**

---

## 🌍 Question 2: After Hosting - Where is Data Stored?

### Scenario A: Hosting on a Server (Recommended)

When you host on a **real server** (like AWS, DigitalOcean, Heroku):

```
┌─────────────────────────────────────────────┐
│  CLOUD SERVER (e.g., AWS)                   │
│                                             │
│  ┌──────────────┐      ┌────────────────┐ │
│  │   Website    │ ←──→ │  MySQL Database│ │
│  │              │      │  (Cloud)       │ │
│  └──────────────┘      └────────────────┘ │
│                                             │
│  ✓ Always running                          │
│  ✓ Data stored on server                   │
│  ✓ Accessible 24/7                         │
└─────────────────────────────────────────────┘
```

**Answer:** Data is stored on the **cloud server's database**, NOT your local computer.

### Scenario B: Keeping Local Database (Not Recommended)

```
┌──────────────┐                    ┌─────────────────┐
│ Cloud Server │ ──── Internet ───→ │ Your Computer   │
│  (Website)   │                    │  (Database)     │
└──────────────┘                    └─────────────────┘
```

**Problems:**
- ❌ Your computer must be ON 24/7
- ❌ Need static IP address
- ❌ Security risks
- ❌ Slow performance

---

## 🚀 Question 3: Do I Need to Keep Code Running?

### Current Situation (Local)
- ✅ **YES** - You must keep `mvn tomcat7:run` running
- ✅ **YES** - MySQL must be running
- ✅ **YES** - Your computer must be ON
- ❌ Only YOU can access it (localhost)

### After Hosting (Production)
- ✅ **NO** - Server runs automatically
- ✅ **NO** - You can turn off your computer
- ✅ **YES** - Anyone can access it (public URL)

---

## 🎯 Solutions for Different Scenarios

### Solution 1: Free Hosting (Best for Learning/Testing)

#### Option A: Railway.app (Recommended)
```bash
# 1. Install Railway CLI
npm install -g @railway/cli

# 2. Login
railway login

# 3. Deploy
railway init
railway up
```

**Includes:**
- ✅ Free MySQL database
- ✅ Automatic deployment
- ✅ Free domain
- ✅ Always running

#### Option B: Heroku (Popular)
```bash
# 1. Install Heroku CLI
brew install heroku/brew/heroku

# 2. Login
heroku login

# 3. Create app
heroku create your-event-app

# 4. Add MySQL
heroku addons:create cleardb:ignite

# 5. Deploy
git push heroku main
```

---

### Solution 2: Professional Hosting

#### AWS (Amazon Web Services)
**Cost:** ~$10-20/month

**Setup:**
1. EC2 instance for application
2. RDS for MySQL database
3. S3 for file storage

**Pros:**
- ✅ Highly scalable
- ✅ Professional grade
- ✅ 99.99% uptime

#### DigitalOcean
**Cost:** ~$5-10/month

**Setup:**
1. Create Droplet (Ubuntu)
2. Install Java, Tomcat, MySQL
3. Deploy your WAR file

**Pros:**
- ✅ Simple to use
- ✅ Good documentation
- ✅ Affordable

---

### Solution 3: Keep Running Locally (Development Only)

If you want to keep it on your computer:

**Requirements:**
1. Computer must be ON 24/7
2. MySQL must be running
3. Application must be running
4. Need port forwarding on router

**Setup:**
```bash
# 1. Get your local IP
ifconfig | grep "inet "

# 2. Configure router port forwarding
# Forward port 7070 to your computer's IP

# 3. Get public IP
curl ifconfig.me

# 4. Share URL
# http://YOUR_PUBLIC_IP:7070/event-management
```

**⚠️ Not Recommended Because:**
- Power cuts will stop it
- Slow internet affects performance
- Security risks
- High electricity cost

---

## 📝 Step-by-Step: Deploy to Cloud (Recommended)

### Using Railway.app (Easiest)

**Step 1: Prepare Your Code**
```bash
cd event-management-system

# Create Procfile
echo "web: java -jar target/event-management-system.war" > Procfile
```

**Step 2: Update Database Config**

Edit `src/main/java/com/eventmanagement/util/DatabaseUtil.java`:

```java
public class DatabaseUtil {
    // Read from environment variables
    private static final String DB_URL = System.getenv("DATABASE_URL");
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    
    // Fallback to local for development
    static {
        if (DB_URL == null) {
            DB_URL = "jdbc:mysql://localhost:3306/event_management";
            DB_USER = "root";
            DB_PASSWORD = "root";
        }
    }
}
```

**Step 3: Deploy**
```bash
# Install Railway
npm install -g @railway/cli

# Login
railway login

# Initialize project
railway init

# Add MySQL
railway add mysql

# Deploy
railway up

# Get URL
railway open
```

**Step 4: Set Up Database**
```bash
# Connect to Railway MySQL
railway connect mysql

# Run schema
source database/schema.sql;
exit;
```

**Done! Your app is live 24/7!**

---

## 🔄 Data Migration: Local to Cloud

### Export Local Database
```bash
# Export your local data
mysqldump -u root -p event_management > backup.sql
```

### Import to Cloud Database
```bash
# For Railway
railway connect mysql < backup.sql

# For Heroku
heroku mysql:import backup.sql

# For AWS RDS
mysql -h your-rds-endpoint.amazonaws.com -u admin -p event_management < backup.sql
```

---

## 📊 Monitoring Your Database

### Real-Time Monitoring Script

Run this to see live database changes:
```bash
./check_database.sh
```

### Manual Checks
```bash
# Check total bookings
mysql -u root -p -e "SELECT COUNT(*) as total FROM event_management.bookings;"

# Check latest booking
mysql -u root -p -e "SELECT * FROM event_management.bookings ORDER BY booking_date DESC LIMIT 1;"

# Check available seats
mysql -u root -p -e "SELECT name, available_seats FROM event_management.events;"
```

---

## 🎯 Recommended Setup for You

Based on your needs, here's what I recommend:

### For Learning/Testing (FREE)
```
1. Use Railway.app or Render.com
2. Free MySQL included
3. Deploy in 5 minutes
4. Get public URL
5. No need to keep computer on
```

### For Production (PAID)
```
1. DigitalOcean Droplet ($5/month)
2. Managed MySQL ($15/month)
3. Professional setup
4. 99.9% uptime
5. Full control
```

---

## ✅ Quick Checklist

### Before Hosting:
- [ ] Test locally: http://localhost:7070/event-management
- [ ] Verify database connection
- [ ] Make a test booking
- [ ] Check data in MySQL
- [ ] Export database backup

### After Hosting:
- [ ] Application accessible via public URL
- [ ] Database connected to cloud
- [ ] Test booking on live site
- [ ] Verify data in cloud database
- [ ] Set up automatic backups

---

## 🆘 Common Questions

### Q: Will I lose my local data after hosting?
**A:** No! Your local data stays on your computer. Cloud has separate database.

### Q: Can I use both local and cloud?
**A:** Yes! Keep local for development, cloud for production.

### Q: Do I need to keep MySQL running after hosting?
**A:** Local MySQL: Only for local testing
**A:** Cloud MySQL: Runs automatically on server

### Q: How to sync local and cloud databases?
**A:** Export local → Import to cloud (see Data Migration section)

---

## 🚀 Next Steps

**Choose your path:**

### Path 1: Quick Test (5 minutes)
```bash
# Verify database is working
./check_database.sh

# Make a booking on website
# Check if it appears in database
```

### Path 2: Deploy to Cloud (30 minutes)
```bash
# Follow "Deploy to Cloud" section above
# Use Railway.app for easiest setup
```

### Path 3: Keep Local (Not Recommended)
```bash
# Keep running: mvn tomcat7:run
# Keep MySQL running
# Keep computer ON
```

---

## 📞 Need Help?

**Database not reflecting changes?**
→ Check DatabaseUtil.java password
→ Run: `./check_database.sh`

**Want to host?**
→ Follow Railway.app steps above
→ Or use Heroku guide

**Need to migrate data?**
→ See "Data Migration" section

---

**Remember:** 
- 💻 Local = Your computer (temporary)
- ☁️ Cloud = Server (permanent, 24/7)
- 🎯 For real users, always use cloud hosting!

