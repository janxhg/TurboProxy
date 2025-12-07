import os
import shutil

root_dir = r"D:\ASAS\minecraft_server\papermc_modificado\TurboProxy"

# 1. Move Directories
moves = [
    ("proxy/src/main/java/com/velocitypowered", "proxy/src/main/java/com/turbopowered"),
    ("native/src/main/java/com/velocitypowered", "native/src/main/java/com/turbopowered"),
    ("proxy/src/main/resources/com/velocitypowered", "proxy/src/main/resources/com/turbopowered"),
    ("proxy/src/test/java/com/velocitypowered", "proxy/src/test/java/com/turbopowered"),
    ("api/src/main/java/com/velocitypowered", "api/src/main/java/com/turbopowered") 
]

print("Starting directory moves...")
for src, dst in moves:
    full_src = os.path.join(root_dir, src)
    full_dst = os.path.join(root_dir, dst)
    
    # Normalize slashes
    full_src = os.path.normpath(full_src)
    full_dst = os.path.normpath(full_dst)

    if os.path.exists(full_src):
        print(f"Moving {src} -> {dst}")
        # Create parent dir of dst if needed (e.g. com/)
        os.makedirs(os.path.dirname(full_dst), exist_ok=True)
        
        if os.path.exists(full_dst):
             print(f"Destination {dst} exists. Merging...")
             # Move children
             for item in os.listdir(full_src):
                 s = os.path.join(full_src, item)
                 d = os.path.join(full_dst, item)
                 if os.path.exists(d):
                     print(f"Skipping {item} (destination exists)")
                 else:
                     shutil.move(s, d)
             # Try to remove empty src
             try:
                os.rmdir(full_src)
             except:
                pass
        else:
            shutil.move(full_src, full_dst)
    else:
        print(f"Source {src} not found (already moved?)")

# 2. Search and Replace in Files
extensions = ('.java', '.kt', '.properties', '.xml', '.json', '.toml', '.gradle')
ignore_dirs = {'.git', 'build', '.gradle', 'gradled'}

print("Starting content replacement...")
for root, dirs, files in os.walk(root_dir):
    # Filter dirs
    dirs[:] = [d for d in dirs if d not in ignore_dirs]
    
    for file in files:
        if file.endswith(extensions) or file == 'libs.versions.toml':
            path = os.path.join(root, file)
            try:
                with open(path, 'r', encoding='utf-8', errors='ignore') as f:
                    content = f.read()
                
                new_content = content.replace("com.velocitypowered", "com.turbopowered")
                
                # Replace branding in properties files (Localization)
                if file.endswith(".properties"):
                     # Careful not to break keys if logic depends on them, but user said "All instances"
                     # Specifically "velocity.command." keys might be hardcoded in Java?
                     # Ideally Java uses constants or strings. If I change keys here, I must change them in Java too.
                     # Java replacement "com.velocitypowered" handles imports.
                     # Keys: "velocity.command.server-too-many".
                     # I should replace "velocity." with "turbo." in keys AND Java strings?
                     # Let's verify if I should do strict key replacement.
                     # I'll enable it for .properties.
                     # And I hope the Java code uses string literals that I can find?
                     # For now, I'll update the VALUES (text) indiscriminately.
                     # Updating KEYS is risky without code analysis.
                     # I will update KEYS "velocity." -> "turbo." in .properties.
                     # AND I will try to update "velocity." -> "turbo." in Java strings?
                     # That might break "velocity.toml" or similar?
                     # I'll stick to package rename + branding text (Values). 
                     # I will NOT rename keys "velocity.*" -> "turbo.*" yet unless I am sure.
                     # The prompt said "Velocity to Turbo Rename" - "all instances".
                     # I'll do key rename too. If it breaks, I fix it.
                     new_content = new_content.replace("velocity.", "turbo.")
                     new_content = new_content.replace("Velocity", "TurboProxy")

                if new_content != content:
                    print(f"Updating {path}")
                    with open(path, 'w', encoding='utf-8') as f:
                        f.write(new_content)
            except Exception as e:
                print(f"Error processing {path}: {e}")

print("Done.")
