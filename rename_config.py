import os

root_dir = r"D:\ASAS\minecraft_server\papermc_modificado\TurboProxy"
extensions = ('.java')
ignore_dirs = {'.git', 'build', '.gradle'}

print("Starting Java string updates...")
for root, dirs, files in os.walk(root_dir):
    dirs[:] = [d for d in dirs if d not in ignore_dirs]
    
    for file in files:
        if file.endswith(extensions):
            path = os.path.join(root, file)
            try:
                with open(path, 'r', encoding='utf-8') as f:
                    content = f.read()
                
                new_content = content
                # Config files
                new_content = new_content.replace('"velocity.toml"', '"turbo.toml"')
                new_content = new_content.replace('"default-velocity.toml"', '"default-turbo.toml"')
                
                # Localization keys (matching properties file updates)
                new_content = new_content.replace('"velocity.command."', '"turbo.command."')
                new_content = new_content.replace('"velocity.error."', '"turbo.error."')
                # Add more key prefixes if known, e.g. "velocity.info."?
                # I'll stick to what I've seen or generic "velocity." in strings is dangerous.
                
                if new_content != content:
                    print(f"Updating {path}")
                    with open(path, 'w', encoding='utf-8') as f:
                        f.write(new_content)
            except Exception as e:
                print(f"Error processing {path}: {e}")
print("Done.")
